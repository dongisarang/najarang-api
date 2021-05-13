package com.najarang.back.service.impl;

import com.najarang.back.advice.exception.CBoardNotFoundException;
import com.najarang.back.advice.exception.CTopicNotFoundException;
import com.najarang.back.dto.BoardDTO;
import com.najarang.back.dto.ImageDTO;
import com.najarang.back.entity.Board;
import com.najarang.back.entity.Topic;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.repo.BoardJpaRepo;
import com.najarang.back.repo.ImageJpaRepo;
import com.najarang.back.repo.TopicJpaRepo;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.BoardService;
import com.najarang.back.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service("boardService")
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardJpaRepo boardJpaRepo;
    private final TopicJpaRepo topicJpaRepo;
    private final ImageJpaRepo imageJpaRepo;
    private final ResponseService responseService;
    private final S3Service s3Service;

    public ListResult<Board> getBoards(Pageable pageable) {
        return responseService.getListResult(boardJpaRepo.findAll(pageable));
    }

    public ListResult<Board> getBoardsByTopicId(long topicId, Pageable pageable) {
        return responseService.getListResult(boardJpaRepo.findByTopicId(topicId, pageable));
    }

    public SingleResult<Board> getBoard(long id) {
        return responseService.getSingleResult(boardJpaRepo.findById(id).orElseThrow(CBoardNotFoundException::new));
    }

    @Transactional
    public SingleResult<BoardDTO> save(BoardDTO board) {
        MultipartFile[] files = Optional.ofNullable(board.getFiles()).orElse(new MultipartFile[]{});
        if(files.length > 0) {
            List<String> images = new ArrayList<>();
            Arrays.asList(files).stream().forEach(file -> {
                // s3에 업로드하고 imageurl 가져오기
                try {
                    images.add(s3Service.upload(file));
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
        board.setTopic(topic);

        Collection<String> imageUrls = Optional.ofNullable(board.getImageUrls()).orElseGet(() -> new ArrayList<>(){});
        Board insertedBoard = boardJpaRepo.save(board.toEntity());
        Long boardId = insertedBoard.getId();
        imageUrls.stream().forEach(imageUrl -> {
            ImageDTO image = new ImageDTO();
            image.setFileName(imageUrl);
            image.setBoardId(boardId);
            imageJpaRepo.save(image.toEntity());
        });

        BoardDTO insertedBoardDTO = insertedBoard.toDTO();
        if(imageUrls.size() > 0) insertedBoardDTO.setImageUrls(imageUrls);
        return responseService.getSingleResult(insertedBoardDTO);
    }

    public SingleResult<Board> modify(BoardDTO board) {
        long boardId = board.getId();
        Optional<Board> newBoard = boardJpaRepo.findById(boardId);
        BoardDTO boardDto = newBoard.get().toDTO();
        if (board.getTitle() != null) boardDto.setTitle(board.getTitle());
        if (board.getContent() != null) boardDto.setContent(board.getContent());
        if (board.getTopicId() != null) {
            Topic topic = topicJpaRepo.findById(boardDto.getTopicId()).orElseThrow(CTopicNotFoundException::new);
            boardDto.setTopic(topic);
        }
        return responseService.getSingleResult(boardJpaRepo.save(boardDto.toEntity()));
    }

    public CommonResult delete(long id) {
        boardJpaRepo.deleteById(id);
        return responseService.getSuccessResult();
    }
}