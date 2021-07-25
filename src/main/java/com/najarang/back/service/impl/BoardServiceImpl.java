package com.najarang.back.service.impl;

import com.najarang.back.advice.exception.CBoardNotFoundException;
import com.najarang.back.advice.exception.CTopicNotFoundException;
import com.najarang.back.dto.BoardDTO;
import com.najarang.back.dto.ImageDTO;
import com.najarang.back.entity.Board;
import com.najarang.back.entity.Image;
import com.najarang.back.entity.Topic;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.repo.BoardJpaRepo;
import com.najarang.back.repo.ImageJpaRepo;
import com.najarang.back.repo.TopicJpaRepo;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.BoardService;
import com.najarang.back.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("boardService")
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardJpaRepo boardJpaRepo;
    private final TopicJpaRepo topicJpaRepo;
    private final ImageJpaRepo imageJpaRepo;
    private final ResponseService responseService;
    private final S3Util s3Util;

    public ListResult<BoardDTO> getBoards(Pageable pageable) {
        Page<Board> pageBoards = boardJpaRepo.findAll(pageable);
        return getBoardDTOListResult(pageable, pageBoards);
    }

    public ListResult<BoardDTO> getBoardsByTopicId(long topicId, Pageable pageable) {
        Page<Board> pageBoards = boardJpaRepo.findByTopicId(topicId, pageable);
        return getBoardDTOListResult(pageable, pageBoards);
    }

    private ListResult<BoardDTO> getBoardDTOListResult(Pageable pageable, Page<Board> pageBoards) {
        List<BoardDTO> boardDTOs = pageBoards.getContent().stream().map((board) -> {
            BoardDTO boardDTO = board.toDTO();
            Collection<Image> images = imageJpaRepo.findByBoardId(boardDTO.getId());
            boardDTO.setImageUrls(images.stream().map(image -> image.getFileName()).collect(Collectors.toList()));
            return boardDTO;
        }).collect(Collectors.toList());
        Page<BoardDTO> pageBoardDTOs = new PageImpl<>(boardDTOs, pageable, pageBoards.getTotalElements());
        return responseService.getListResult(pageBoardDTOs);
    }

    public SingleResult<BoardDTO> getBoard(long id) {
        Board board = boardJpaRepo.findById(id).orElseThrow(CBoardNotFoundException::new);
        Collection<Image> images = imageJpaRepo.findByBoardId(id);
        BoardDTO boardDTO = board.toDTO();
        boardDTO.setImageUrls(images.stream().map(image -> image.getFileName()).collect(Collectors.toList()));
        boardDTO.setImages(null);
        return responseService.getSingleResult(boardDTO);
    }

    // @Transactional : 트랜잭션이 적용된 범위에서는 트랜잭션 기능이 포함된 프록시 객체가 생성되어 자동으로 commit 혹은 rollback을 진행
    // - JPA의 구현체를 살펴보면 모든 메서드에 이미 @Transactional이 선언되어 있기 때문에 
    //   문제가 발생하면 Rollback 처리. 따라서 save()등 기본에는 따로 추가하지않음
    // - 기본적으로 Unchecked Exception, Error 만 rollback
    // - rollbackFor : 모든 예외에 대해서 rollback을 진행하기 위해 추가함 for Checked Exception. 특정 예외 발생 시 rollback을 시켜주는 기능이 있음
    @Transactional(rollbackFor = Exception.class)
    public SingleResult<BoardDTO> save(BoardDTO board) {
        MultipartFile[] files = Optional.ofNullable(board.getFiles()).orElse(new MultipartFile[]{});
        if(files.length > 0) {
            List<String> images = new ArrayList<>();
            Arrays.asList(files).stream().forEach(file -> {
                // s3에 업로드하고 imageurl 가져오기
                try {
                    images.add(s3Util.upload(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            if(!images.isEmpty()) {
                board.setImageUrls(images);
            }
        }
        Long topicId = board.getTopicId();
        Topic topic = topicJpaRepo.findById(topicId).orElseThrow(CTopicNotFoundException::new);
        board.setTopic(topic.toDTO());

        Collection<String> imageUrls = Optional.ofNullable(board.getImageUrls()).orElseGet(() -> new ArrayList<>(){});
        Board insertedBoard = boardJpaRepo.save(board.toEntity());
        Long boardId = insertedBoard.getId();
        imageUrls.stream().forEach(imageUrl -> {
            ImageDTO image = new ImageDTO();
            image.setFileName(imageUrl);
            image.setBoard(board);
            imageJpaRepo.save(image.toEntity());
        });

        BoardDTO insertedBoardDTO = insertedBoard.toDTO();
        if(imageUrls.size() > 0) insertedBoardDTO.setImageUrls(imageUrls);
        return responseService.getSingleResult(insertedBoardDTO);
    }

    public SingleResult<BoardDTO> modify(BoardDTO board) {
        long boardId = board.getId();
        Optional<Board> newBoard = boardJpaRepo.findById(boardId);
        BoardDTO boardDto = newBoard.get().toDTO();
        if (board.getTitle() != null) boardDto.setTitle(board.getTitle());
        if (board.getContent() != null) boardDto.setContent(board.getContent());
        if (board.getTopicId() != null) {
            Topic topic = topicJpaRepo.findById(boardDto.getTopicId()).orElseThrow(CTopicNotFoundException::new);
            boardDto.setTopic(topic.toDTO());
        }
        BoardDTO insertedBoard = boardJpaRepo.save(boardDto.toEntity()).toDTO();
        if(insertedBoard.getImages().size() > 0) {
            insertedBoard.setImageUrls(insertedBoard.getImages().stream().map(image -> image.getFileName()).collect(Collectors.toList()));
        }
        insertedBoard.setImages(null);
        return responseService.getSingleResult(insertedBoard);
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult delete(long id) throws Exception {
        Collection<Image> images = imageJpaRepo.findByBoardId(id);
        s3Util.deleteFile(images);
        boardJpaRepo.deleteById(id);
        return responseService.getSuccessResult();
    }
}