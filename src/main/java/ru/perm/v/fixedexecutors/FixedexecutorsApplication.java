package ru.perm.v.fixedexecutors;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SpringBootApplication
public class FixedexecutorsApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(FixedexecutorsApplication.class);
    static volatile Integer counter = 0;
    @Autowired
    XStream xstream;
    @Autowired
    ArticleRepository articleRepository;

    public static void main(String[] args) {
        SpringApplication.run(FixedexecutorsApplication.class, args);
    }

    /**
     * Получение Xml из файла
     *
     * @param filePath - путь к файлу
     * @return - объект из xml
     */
    ArticleXml getArticleXml(Path filePath) {
        File file = new File(filePath.toUri());
        return (ArticleXml) xstream.fromXML(file);
    }

    /**
     * Запуск обработки в одном потоке
     *
     * @throws URISyntaxException - не найден uri
     * @throws IOException        - ошибка доступа к файлу
     */
    private void runStream() throws URISyntaxException, IOException {
        logger.info("===================================");
        Instant start = Instant.now();
        Path pathXmls = Paths.get(getClass().getClassLoader()
                .getResource("xmls/").toURI());
        List<ArticleXml> articles = Files.walk(pathXmls)
                .filter(path -> path.toFile().isFile())
                .map(this::getArticleXml)
                // Сохранение в базе
                .peek(articleXml -> articleRepository.save(
                        new Article(null, articleXml.getTopic(), articleXml.getContent())
                ))
                .collect(Collectors.toList());
        logger.info(String.format("step:%d", articles.size()));
        Instant end = Instant.now();
        logger.info(String.format("Time:%d Articles:%d",
                Duration.between(start, end).toMillis(),
                articleRepository.findAll().size()));
    }

    /**
     * Подучение объекта из xml-файла асинхронно
     *
     * @param filePath  - путь к файлу
     * @param executors - executor
     * @return - объект из xml
     */
    CompletableFuture<ArticleXml> getFeatureArticleXml(Path filePath, ExecutorService executors) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(filePath.toUri());
//            logger.info(filePath.toString());
            return (ArticleXml) xstream.fromXML(file);
        }, executors);
    }

    /**
     * Сохранение в базе асинхронно
     *
     * @param articleXml - объект из xml
     * @param executors  - executor
     * @return - сохраненный объект
     */
    CompletableFuture<Article> saveArticle(ArticleXml articleXml, ExecutorService executors) {
        return CompletableFuture.supplyAsync(() -> {
//            logger.info(articleXml.toString());
            synchronized (this) {
                logger.info(String.format("counter:%d", counter++));
            }
            Article article = articleRepository.save(
                    new Article(null, articleXml.getTopic(), articleXml.getContent())
            );
//            logger.info(String.format("Article thread:%d id=%d",
//                    Thread.currentThread().getId(),
//                    article.getId()
//                    )
//            );
            return article;
        }, executors);
    }

    /**
     * Запуск обработки во многих потоках асинхронно
     *
     * @throws URISyntaxException - неверный URI
     * @throws IOException        - не наден файл
     */
    private void runFixedExecutor() throws URISyntaxException, IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Instant start = Instant.now();
        Path pathXmls = Paths.get(getClass().getClassLoader()
                .getResource("xmls/").toURI());

        List<CompletableFuture<Article>> futureArticle = Files.walk(pathXmls)
                .filter(path -> path.toFile().isFile())
                .map(path -> {
//            logger.info("Map->" + path.toString());
                    return getFeatureArticleXml(path, executorService)
                            .thenCompose(articleXml -> saveArticle(articleXml, executorService));
                })
                .collect(Collectors.toList());
        ;
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futureArticle.toArray(new CompletableFuture[0])
        );
//        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
//                futureArticle.toArray(new CompletableFuture[futureArticle.size()])
//        );
        allFutures.join();
        executorService.shutdown();
        Instant end = Instant.now();
        logger.info(String.format("Time:%d Articles:%d counter:%d",
                Duration.between(start, end).toMillis(),
                articleRepository.findAll().size(), counter));
    }

    @Override
    public void run(String... args) throws Exception {
        // Очистка базы
        articleRepository.deleteAll();
        // Запуск в одном потоке
//        runStream();
        // Запуск асинхроннов многопотоковом режиме
        runFixedExecutor();
    }
}
