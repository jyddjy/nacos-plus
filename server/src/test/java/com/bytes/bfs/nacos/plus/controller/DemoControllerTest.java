//package com.bytes.bfs.nacos.plus.controller;
//
//import com.bytes.bfs.nacos.plus.test.BaseTest;
//import org.junit.jupiter.api.Test;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//class DemoControllerTest extends BaseTest {
//    @Test
//    void hello() throws Exception {
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/demo/index");
//
//        mockMvc.perform(request).andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").value("hello world!"));
//    }
//
//    @Test
//    void get() throws Exception {
////        MockHttpServletRequestBuilder request = buildJsonRequest("/demo/get", new JSONObject()
////                .fluentPut("orderNo", "test")
////                .fluentPut("payType", "test"));
////
////        mockMvc.perform(request).andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.data").isEmpty());
//    }
//
//    @Test
//    void getFromRedis() throws Exception {
//        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/demo/redis/get")
//                .param("key", "testKey");
//
//        mockMvc.perform(request).andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data").isEmpty());
//    }
//}