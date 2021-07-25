package com.najarang.back.dto;

import com.najarang.back.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageDTO extends BaseTimeDTO {
    private Long id;
    private String fileName;
    private Long boardId;

    public ImageDTO(Image image){
        this.id = image.getId();
        this.fileName = image.getFileName();
        this.boardId = image.getBoardId();
        setCreated(image.getCreated());
        setModified(image.getModified());
    }

    public Image toEntity(){
        Image image = Image.builder()
                .id(id)
                .fileName(fileName)
                .boardId(boardId)
                .build();
        image.setCreated(getCreated());
        image.setModified(getModified());
        return image;
    }
}
