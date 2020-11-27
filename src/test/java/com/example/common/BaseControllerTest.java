package com.example.common;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.events.EventRespository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
//통합 테스트 많은 빈들을 등록
//@SpringBootApplication 애노테이션을 찾아서 모든 Bean들을 등록해준다.
@SpringBootTest
@AutoConfigureMockMvc
//웹과 관련되 테스트만 하기때문에 슬라이싱 테스트라고 불림
//웹과 관련된 빈들만 만들기때문에
//조금 더 빠르다? 조금 더 구역을 나눠서 테스트한다?
//단위 테스트로 보기는 힘들다
//@WebMvcTest
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore // 테스트 코드를 가지고 있는 class가 아니다. 테스트를 실행하려고 하면 안된다.
public class BaseControllerTest {
	
	@Autowired
	protected MockMvc mockMvc;
	
	@Autowired
	protected ObjectMapper objectMapper;
	
	@Autowired
	protected ModelMapper modelMapper;
}
