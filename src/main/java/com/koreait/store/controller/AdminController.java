package com.koreait.store.controller;

import com.koreait.store.dto.FileDTO;
import com.koreait.store.dto.ProductDTO;
import com.koreait.store.service.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/upload")
    public void get_upload() {}

    @PostMapping("/upload")
    public String post_upload(ProductDTO productDTO){
        adminService.add_product(productDTO);
        return "redirect:/admin/upload";
    }


    /****************************************************/
    /*** 연습용 ***/
    @GetMapping("/fileupload")
    public void get_fileupload() {}

    @PostMapping("/fileconvert")
    public void post_fileconvert(ProductDTO productDTO) {

    }

    @PostMapping("/fileupload")
    public void post_fileupload(List<MultipartFile> my_files) throws Exception {
//        my_file.getName(); // my_file
        for(MultipartFile my_file : my_files) {
            log.error("전송된 파일명: " + my_file.getOriginalFilename());
            log.error("전송된 파일의 크기: " + my_file.getSize());
            log.error("전송된 파일의 형태: " + my_file.getContentType());
            adminService.add_file(my_file.getBytes());
    //        log.error("전송된 파일의 byte 배열: " + Arrays.toString(my_file.getBytes()));

//            if(my_file.getContentType().startsWith("image")) {
//                File file = new File(my_file.getOriginalFilename());
//                my_file.transferTo(file);
//            }
        }

//        Resource resource = new ClassPathResource("files");
//
//        File resourceDir = resource.getFile();
//        File fileDir = new File(resourceDir, "my_file" + "." + extension);
//        byte[] bytes = my_file.getBytes();
//        FileOutputStream fos = new FileOutputStream(fileDir);
//        fos.write(bytes);
//        fos.close();
    }

    @GetMapping("/filedownload")
    public void get_filedownload() {}

    @ResponseBody
    @GetMapping("/db/{no}")
    public ResponseEntity<byte[]> get_Db_file_download(
            @PathVariable("no") Integer fileNo
    ){
        FileDTO fileDTO = adminService.get_file(fileNo);
        byte[] fileData = fileDTO.getData();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(fileData.length);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("1.jpg").build()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(fileData);
    }

//    @GetMapping("/image")
//    public void image(HttpServletResponse response) {
//        try{
//            OutputStream out = response.getOutputStream();
//            File file = new File("C:\\Users\\yegkw\\Desktop\\웹개발3\\Cookie Run Store\\src\\main\\resources\\files\\1.png");
//            FileInputStream in = new FileInputStream(file);
//            byte[] bytes = in.readAllBytes();
//
//            response.setContentType("image/jpeg");
//            response.setContentLength(bytes.length);
//            response.setHeader("Content-Disposition", "attachment; filename=1.jpg");
//
//            out.write(bytes);
//            in.close();
//            out.close();
//        } catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//    }

    @GetMapping("/image/{filename}")
    @ResponseBody
    public byte[] get_image(
            @PathVariable("filename") String filename,
            HttpServletResponse response
    ){
        try {
            File file = new File("C:\\Users\\yegkw\\Desktop\\웹개발3\\Cookie Run Store\\src\\main\\resources\\files\\1.png");
            FileInputStream in = new FileInputStream(file);
            byte[] bytes = in.readAllBytes();
            response.setContentType("image/jpeg");
            response.setContentLength(bytes.length);
            response.setHeader("Content-Disposition", "attachment;filename=1.jpg");
            in.close();

            return bytes;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}

//사용자가 파일을 서버에 전달하고, 서버는 그 파일을 어떻게 사용할 것인가?
//1. 프로젝트 내에 어떤 폴더에 그대로 저장해서 사용하기
//   (대체로 resources 폴더가 된다) - 연습용으로만 사용하지 누구도 사용안함
//2. 서버 컴퓨터의 어떤 폴더에 그대로 저장해서 사용하기
//   (C 드라이브의 어떤 폴더)
//3. db에 파일 byte를 사용해서 blob형태로 바로 저장하기
//3.1 파일은 컴퓨터에 저장하고 dv에 해당 파일의 경로만 archar로 저장하기
//4. 웹서버 말고 리소스 서버 컴퓨터를 따로 둔 다음에 해당 컴퓨터에 저장하기

// 1. 파일명을 중복되지 않는 값(ex. USER의 PK) + 파일명 + 고유값(시간값, UUID)
// 2. 폴더로 나누기 ... /유저1/1_uuid.jpg  /유저2/1_uuid.jpg
