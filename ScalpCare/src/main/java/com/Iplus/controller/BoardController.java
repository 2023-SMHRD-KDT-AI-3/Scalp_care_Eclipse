package com.Iplus.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Iplus.entity.tb_admin_scalp_care;
import com.Iplus.entity.tb_member;
import com.Iplus.entity.tb_user_scalp_care;
import com.Iplus.repository.UserScalpCareRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;

@Controller
@RestController
public class BoardController {
	
	@Autowired
	private UserScalpCareRepository repo;

	// 넘겨받은 값을 가져올 때 매개변수의 이름은 key와 동일하게 작성해야 한다.
	@RequestMapping("/Boardsave")
	public void Boardsave(String content, String img, String ucUid, String indate, String result) {

		// UUID 생성해서 이름 설정하기
		String img_name = UUID.randomUUID().toString();
		
		// 이미지 경로 설정
		String savePath = "C:/Users/smhrd/Desktop/project/user_scalp_img/"+ img_name +".png";
		
		// Base64 공백 제거
		img = img.replaceAll("\\s+", "");
		System.out.println(img.length());
		
		byte[] decodeBytes = Base64.getDecoder().decode(img.getBytes());
				
		// 이미지 저장
		try {
            ByteArrayInputStream bis = new ByteArrayInputStream(decodeBytes);
            BufferedImage image = ImageIO.read(bis);
            
            // 이미지를 파일로 저장 (예: PNG 형식)
            File outputFile = new File(savePath);
            ImageIO.write(image, "png", outputFile);
            
            System.out.println("Image saved successfully!");
        } catch (IOException e) {
            System.out.println("Error converting byte array to image: " + e.getMessage());
        }
		
		tb_user_scalp_care sc_care = new tb_user_scalp_care();
		
		tb_member member = new tb_member();
		member.setUid(ucUid);
        
		sc_care.setUcUid(member);
		sc_care.setImg(savePath);
		sc_care.setContent(content);
		sc_care.setResult(result);
		
		repo.save(sc_care);

	}
		
	@RequestMapping("/Boardview")
	public List<String> Boardview(String ucUid) {

		tb_member member = new tb_member();
		member.setUid(ucUid);

		// 게시글 불러오기
		List<tb_user_scalp_care> uc_board = repo.Boardview(member);
		
		// 객체 → Json(String)
		ObjectMapper objectMapper = new ObjectMapper();
		
		// String List
		List<String> jsonList = new ArrayList<>();
		String jsonString ;
			try {
				for (tb_user_scalp_care obj : uc_board) {
				
					// 객체 → Json형태 String → StringList에 담음
					jsonString = objectMapper.writeValueAsString(obj);
					jsonList.add(jsonString);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return jsonList;	
	}	
	
	@RequestMapping("/getImage")
	public String getImage(String ucNum) {		
		// DB에서 경로 받아옴
		System.out.println("여기 오냐?" +ucNum);
		

		tb_user_scalp_care uc_board = repo.findByUcNum(Long.valueOf(ucNum));
		String base64_img = null;
		
	
		// 객체 → Json(String)
		ObjectMapper objectMapper = new ObjectMapper();
				
		
			try {
				// 경로를 이용하여 이미지 가져오기
				byte[] imageBytes = Files.readAllBytes(Paths.get(uc_board.getImg()));
				
				// Base64로 인코딩
				base64_img = Base64.getEncoder().encodeToString(imageBytes);	
				System.out.println(base64_img.length());
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		return base64_img;	
	}	
	
	// 최근 게시글 가져오기
	@RequestMapping("/getBoardDataRecent")
		public List<String> getBoardDataRecent(String ucUid) {
			
			tb_member member = new tb_member();
			member.setUid(ucUid);
			
			List<tb_user_scalp_care> bc_board = repo.Boardview(member);	
			System.out.println("여기에 최근글 가져오고 싶당?" + bc_board);		
			
			// 객체 → Json(String)
			ObjectMapper objectMapper = new ObjectMapper();
			
			// String List
			List<String> jsonList = new ArrayList<>();
			String jsonString;
				try {
					for(tb_user_scalp_care obj : bc_board) {
						System.out.println(obj);
						// 객체 → Json형태 String → StringList에 담음 
						jsonString = objectMapper.writeValueAsString(obj);
						jsonList.add(jsonString);
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			return jsonList;
		}
	
	// 날짜 필터(검색)
	@RequestMapping("/DateView")
	public List<String> DateView(String ucUid, String startDate, String endDate) {
		
		// startDate와 endDate를 Date형식(java.util.Date)으로 변환
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Date start = null;
	    Date end = null;
	    try {
	        start = sdf.parse(startDate);
	        end = sdf.parse(endDate);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }

		tb_member member = new tb_member();
		member.setUid(ucUid);

		// 게시글 불러오기
		List<tb_user_scalp_care> uc_board = repo.DateView(member, start, end);
		
		// 객체 → Json(String)
		ObjectMapper objectMapper = new ObjectMapper();
		
		// String List
		List<String> jsonList = new ArrayList<>();
		String jsonString ;
			try {
				for (tb_user_scalp_care obj : uc_board) {
				
					// 객체 → Json형태 String → StringList에 담음
					jsonString = objectMapper.writeValueAsString(obj);
					jsonList.add(jsonString);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return jsonList;	
	}
	
	@RequestMapping("/boardDelete")
	public void boardDelete(String ucNum) {
	
		System.out.println("삭제시작함");
		
		repo.deleteById(Long.valueOf(ucNum));
		
	}
	
	@RequestMapping("/resave")
	public void resave(String ucNum, String content, String img, String result) {
		
		System.out.println("여기오는거 맞지?");
		
		// UUID 생성해서 이름 설정하기
		String img_name = UUID.randomUUID().toString();
		
		// 이미지 경로 설정
		String savePath = "C:/Users/smhrd/Desktop/project/user_scalp_img/"+ img_name +".png";
		
		// Base64 공백 제거
		img = img.replaceAll("\\s+", "");
		System.out.println(img.length());
		
		byte[] decodeBytes = Base64.getDecoder().decode(img.getBytes());
				
		// 이미지 저장
		try {
            ByteArrayInputStream bis = new ByteArrayInputStream(decodeBytes);
            BufferedImage image = ImageIO.read(bis);
            
            // 이미지를 파일로 저장 (예: PNG 형식)
            File outputFile = new File(savePath);
            ImageIO.write(image, "png", outputFile);
            
            System.out.println("Image saved successfully!");
        } catch (IOException e) {
            System.out.println("Error converting byte array to image: " + e.getMessage());
        }

		Optional<tb_user_scalp_care> optional = repo.findById(Long.valueOf(ucNum));
		tb_user_scalp_care uc = null;
		if(optional.isPresent()) {
			uc = optional.get();
		} 
		
		uc.setImg(savePath);
		uc.setContent(content);
		uc.setResult(result);
		
		repo.save(uc);
		
		
		
	}
	
}
