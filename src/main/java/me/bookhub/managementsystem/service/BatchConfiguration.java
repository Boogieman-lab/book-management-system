package me.bookhub.managementsystem.service;

import lombok.RequiredArgsConstructor;
//import me.bookhub.managementsystem.domain.Book;
//import me.bookhub.managementsystem.repository.BookRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
@Service
public class BatchConfiguration {

//    @Autowired
//    public JobBuilderFactory jobBuilderFactory;
//
////    @Autowired
//    public StepBuilderFactory stepBuilderFactory;
//
////    @Autowired
//    private AladinApiClient aladinApiClient;
//
////    @Autowired
//    private BookRepository bookRepository;
//
//    @Bean
//    public Job updateBookJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return jobBuilderFactory.get("updateBookJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(updateBookStep(jobRepository, transactionManager))
//                .end()
//                .build();
//    }
//
//    @Bean
//    public Step updateBookStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        return stepBuilderFactory.get("updateBookStep")
//                .<Book, Book>chunk(10)
//                .reader(bookReader())
//                .processor(bookProcessor())
//                .writer(bookWriter())
//                .build();
//    }
//
//    @Bean
//    public Tasklet bookReader() {
//        return (contribution, chunkContext) -> {
//            List<Book> books = aladinApiClient.fetchBooks();
//            chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("books", books);
//            return RepeatStatus.FINISHED;
//        };
//    }
//
//    @Bean
//    public Tasklet bookProcessor() {
//        return (contribution, chunkContext) -> {
//            List<Book> books = (List<Book>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("books");
//            books.forEach(book -> {
//                if (!bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
//                    bookRepository.save(book);
//                }
//            });
//            return RepeatStatus.FINISHED;
//        };
//    }

    @Bean
    public Tasklet bookWriter() {
        return (contribution, chunkContext) -> RepeatStatus.FINISHED;
    }

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 0시 실행
    public void perform(JobLauncher jobLauncher, Job updateBookJob) throws Exception {
        jobLauncher.run(updateBookJob, new JobParameters());
    }
}
