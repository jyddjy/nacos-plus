//package com.bytes.bfs.nacos.plus.test;
//
//import com.alibaba.fastjson.JSONObject;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.util.AopTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest(classes = {TestConfig.class, ClientStubs.class})
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("unittest")
//public abstract class BaseTest {
//    @Autowired
//    protected MockMvc mockMvc;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    protected <T> T unwrapProxy(Object bean) {
//        return AopTestUtils.getUltimateTargetObject(bean);
//    }
//
//    protected MockHttpServletRequestBuilder buildJsonRequest(String path, Object object) {
//        return MockMvcRequestBuilders
//                .post(path)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(JSONObject.toJSONString(object));
//    }
//}
