package k8sbook.sampleapp;

import com.amazonaws.services.s3.AmazonS3;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import io.findify.s3mock.S3Mock;
import k8sbook.sampleapp.BatchApplication;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.db.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class BatchApplicationTest {

    private static boolean needToInitializeS3 = true;

    private static S3Mock s3Mock;

    @Autowired
    private BatchApplication batchApp;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AmazonS3 amazonS3;

    /**
     * 배치 애플리케이션 초기화 시에는 처리(BatchApplication#run)이 동작하지 않게 된다.
     */
    @BeforeAll
    public static void prepareBatchNotToRun() {
        System.setProperty("sample.app.batch.run", "false");
    }

    @Value("${sample.app.batch.bucket.name}")
    private String bucketName;

    @Value("${sample.app.batch.folder.name}")
    private String folderName;

    @BeforeAll
    public static void startS3Mock() {
        s3Mock = new S3Mock.Builder()
                .withPort(8001)
                .withInMemoryBackend()
                .build();
        s3Mock.start();
    }

    /**
     * 테스트 시에는 배치 애플리케이션 처리(BatchApplication#run)가 동작하게 된다.
     */
    @BeforeEach
    public void prepareBatchToRun() {
        System.setProperty("sample.app.batch.run", "true");
    }

    @AfterAll
    public static void shutdownS3Mock() {
        if (s3Mock != null) {
            s3Mock.shutdown();
        }
    }

    @BeforeEach
    public void putTestFilesToS3() {
        if (needToInitializeS3) {
            amazonS3.createBucket(bucketName);
            try {
                amazonS3.putObject(bucketName, folderName + "/location1.csv",
                        resourceLoader.getResource("classpath:"
                                + getClass().getPackageName().replace('.', '/')
                                + "/location1.csv").getFile());
                amazonS3.putObject(bucketName, folderName + "/location2.csv",
                        resourceLoader.getResource("classpath:"
                                + getClass().getPackageName().replace('.', '/')
                                + "/location2.csv").getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            needToInitializeS3 = false;
        }
    }

    @Tag("DBRequired")
    @Test
    public void testRun() throws Exception {
        batchApp.run();

        var locationTable = new Table(dataSource, "location");
        assertThat(locationTable).hasNumberOfRows(8); // original 4 + file1 3 + file2 1
    }

    @BeforeEach
    public void prepareDatabase() {
        var operations = sequenceOf(
                deleteAllFrom("location"),
                deleteAllFrom("region"),
                insertInto("region")
                        .columns("region_id", "region_name", "creation_timestamp")
                        .values(1, "지역1", LocalDateTime.now())
                        .values(2, "지역2", LocalDateTime.now())
                        .values(3, "지역3", LocalDateTime.now())
                        .values(4, "지역4", LocalDateTime.now())
                        .build(),
                insertInto("location")
                        .columns("location_id", "location_name", "region_id", "note")
                        .values(1, "지점1", 1, "지점1의 상세입니다.")
                        .values(2, "지점2", 1, "지점2의 상세입니다.")
                        .values(3, "지점3", 1, "지점3의 상세입니다.")
                        .values(4, "지점4", 1, "지점4의 상세입니다.")
                        .build()
        );
        var dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();

    }

}
