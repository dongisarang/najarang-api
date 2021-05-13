package com.najarang.back.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.UUID;

@Service
@NoArgsConstructor
public class S3Service {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 의존성 주입이 이루어진 후 초기화를 수행하는 메서드, bean이 한 번만 초기화 될수 있도록 함
    @PostConstruct
    public void setS3Client() {
        // 자격 증명 객체를 얻기
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        // AmazonS3ClientBuilder를 통해 S3Client를 가져옴
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        String newFileName = UUID.randomUUID().toString() + extension;

        // 업로드를 하기위해 사용되는 함수
        s3Client.putObject(new PutObjectRequest(bucket, newFileName, file.getInputStream(), null)
                // 외부 공개 이미지이므로 public read 권한을 줌
                .withCannedAcl(CannedAccessControlList.PublicRead));
        // 업로드를 한 후, 해당 url을 db에 저장할 수 있도록 url 반환
        return s3Client.getUrl(bucket, newFileName).toString();
    }
}