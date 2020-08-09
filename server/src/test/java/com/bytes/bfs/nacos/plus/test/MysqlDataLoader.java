//package com.bytes.bfs.nacos.plus.test;
//
//import com.google.common.base.Joiner;
//import com.google.common.cache.CacheBuilder;
//import com.google.common.cache.CacheLoader;
//import com.google.common.cache.LoadingCache;
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.LinkedListMultimap;
//import com.google.common.collect.Multimap;
//import com.google.common.io.Files;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.util.ResourceUtils;
//
//import java.io.File;
//import java.nio.charset.Charset;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * mysql 测试数据加载
// * <p>
// * 测试数据后缀.sql, 默认加载 DEFAULT section 的数据. 如果需要加载指定section的数据需要带上sectionName
// *
// * <p>
// * 文件中通过 "#-->"来定义section. loader可以加载指定section的数据.
// * </p>
// * <p>
// * 文件路径: test/resources/mysql/${testClassName}.sql.
// * <p>
// * 数据格式如下
// * <pre>
// * #-->DEFAULT
// * sql statement1;
// * sql statement2;
// *
// * #-->section1
// * sql statement3;
// * sql statement4;
// *
// * #-->section2
// * sql statement5;
// * </pre>
// *
// * @author chenjun
// */
//@Slf4j
//public class MysqlDataLoader {
//
//    private final static String MYSQL_DATA_PATH = "classpath:mysql/";
//    private final static String MYSQL_FILE_SUFFIX = ".sql";
//    private final static String DEFAULT_SECTION = "DEFAULT";
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private LoadingCache<File, Multimap<String, String>> sqlFileCaches = CacheBuilder.newBuilder()
//            .build(new CacheLoader<File, Multimap<String, String>>() {
//                @SuppressWarnings("NullableProblems")
//                public Multimap<String, String> load(File file) throws Exception {
//                    List<String> lines = Files.asCharSource(file, Charset.forName("utf-8")).readLines();
//                    String sectionFlag = "#-->";
//                    Multimap<String, String> sections = LinkedListMultimap.create();
//                    String currentSectionName = "";
//                    // 按照section分组.
//                    for (String line : lines) {
//                        line = line.trim();
//                        if (StringUtils.isAllBlank(line)) {
//                            continue;
//                        }
//                        String sectionName = StringUtils.substringAfter(line, sectionFlag);
//                        if (org.apache.logging.log4j.util.Strings.isNotBlank(sectionName)) {
//                            currentSectionName = sectionName;
//                        } else {
//                            sections.put(currentSectionName, line);
//                        }
//                    }
//                    return sections;
//                }
//            });
//
//    public void loadFromClassName(String className) {
//        String locationPattern = MYSQL_DATA_PATH + className + MYSQL_FILE_SUFFIX;
//        File file = getFile(locationPattern);
//        execStatements(file, ImmutableList.of(DEFAULT_SECTION));
//    }
//
//
//    public void loadFromClassName(String className, List<String> includeSections) {
//        String locationPattern = MYSQL_DATA_PATH + className + MYSQL_FILE_SUFFIX;
//        File file = getFile(locationPattern);
//        execStatements(file, includeSections);
//    }
//
//    public void load(String filePath) {
//        String locationPattern = MYSQL_DATA_PATH + filePath;
//        File file = getFile(locationPattern);
//        execStatements(file, ImmutableList.of(DEFAULT_SECTION));
//    }
//
//    public void load(String filePath, List<String> includeSections) {
//        String locationPattern = MYSQL_DATA_PATH + filePath;
//        File file = getFile(locationPattern);
//        execStatements(file, includeSections);
//    }
//
//    private File getFile(String locationPattern) {
//        File file;
//        try {
//            file = ResourceUtils.getFile(locationPattern);
//        } catch (Exception ex) {
//            throw new RuntimeException("try get file error", ex);
//        }
//        return file;
//    }
//
//    private void execStatements(File file, List<String> includeSections) {
//        Multimap<String, String> sections = sqlFileCaches.getUnchecked(file);
//        List<String> statements = sections.entries().stream()
//                .filter(e -> includeSections.isEmpty() || includeSections.contains(e.getKey()))
//                .map(Map.Entry::getValue)
//                .collect(Collectors.toList());
//        String sql = Joiner.on("\n").join(statements);
//        log.info("\n>>>>>>>>>>>>>>> Execute SQL {}\n {}", Joiner.on(",").join(includeSections), sql);
//        jdbcTemplate.update(sql);
//        log.info("<<<<<<<<<<<<< Execute DONE\n");
//    }
//
//}
