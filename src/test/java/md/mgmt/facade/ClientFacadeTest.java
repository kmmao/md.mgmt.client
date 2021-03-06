package md.mgmt.facade;

import md.mgmt.base.md.MdAttr;
import md.mgmt.base.md.MdIndex;
import md.mgmt.facade.req.Md;
import md.mgmt.facade.resp.FindDirMdResp;
import md.mgmt.service.CreateMdService;
import md.mgmt.service.FindMdService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by Mr-yang on 16-1-9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class ClientFacadeTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientFacadeTest.class);

    @Autowired
    private ClientFacade clientFacade;

    @InjectMocks
    @Autowired
    private CreateMdService createMdService;

    @Autowired
    private FindMdService findMdService;

    //    @Mock
//    private CreateMdDao createMdDao;
    private Md md = new Md();
    private MdIndex mdIndex = new MdIndex();
    private MdAttr mdAttr = new MdAttr();

    @Before
    public void initMocks() {
        mdAttr.setAcl((short) 777);
    }

    @Test
    public void buildDirTree() {
        long start = System.currentTimeMillis();
        String secondDir = "bin";
        for (int i = 0; i < 5; i++) {
            createMdService.createDirMd(getMd("/", secondDir + i, i));

        }

        long end = System.currentTimeMillis();
        System.out.println(String.valueOf(System.currentTimeMillis()));
        System.out.println(String.format("time: %s", (end - start)));

        String thirdDir = "foo";
        String thirdFile = "a.t";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                createMdService.createDirMd(getMd("/" + secondDir + i, thirdDir + j + ":" + i, j));
                createMdService.createFileMd(getMd("/" + secondDir + i, thirdFile + j, j * 5));
            }
        }
        long end2 = System.currentTimeMillis();
        System.out.println(String.valueOf(System.currentTimeMillis()));
        System.out.println(String.format("time: %s", (end2 - end)));
        System.out.println(String.format("total time: %s", (end2 - start)));
    }

    @Test
    public void testListDirMd() {
        mdIndex.setPath("/");
        mdIndex.setName("bin2");
        FindDirMdResp findDirMdResp = findMdService.findDirMd(mdIndex);
        mdIndex.setName("");
        findDirMdResp = findMdService.findDirMd(mdIndex);
        printDirList(findDirMdResp);
    }

    private Md getMd(String path, String name, int size) {
        mdIndex.setPath(path);
        mdIndex.setName(name);
        mdAttr.setName(name);
        mdAttr.setSize(size);
        md.setMdAttr(mdAttr);
        md.setMdIndex(mdIndex);
        return md;
    }

    @Test
    public void testCreateFileMd() {
        mdIndex.setPath("/bin/yang");
        mdIndex.setName("f1");
        mdAttr.setName("f1");
        md.setMdAttr(mdAttr);
        md.setMdIndex(mdIndex);
        System.out.println(clientFacade.createFileMd(md));
        mdIndex.setPath("/bin/yang");
        mdIndex.setName("f2");
        mdAttr.setName("f2");
        md.setMdAttr(mdAttr);
        md.setMdIndex(mdIndex);
        System.out.println(clientFacade.createFileMd(md));
        /*int count = 10;
        System.out.println("\n\n\n" + String.valueOf(System.currentTimeMillis()));
        long start = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            System.out.println(clientFacade.createFileMd(md));
        }
        long end = System.currentTimeMillis();
        System.out.println(String.valueOf(System.currentTimeMillis()));
        System.out.println(
                String.format("\nCreate %s dir use Total time: %s ms\navg time: %sms\n\n\n",
                        count, (end - start), (end - start) / (count * 1.0)));*/
    }

    @Test
    public void testCreteDirMd() {
        mdIndex.setPath("/bin");
        mdIndex.setName("yang");
        md.setMdIndex(mdIndex);
        md.setMdAttr(mdAttr);
        logger.info(createMdService.createDirMd(md).toString());
    }

    @Test
    public void testFindFileMd() {
        mdIndex.setPath("/bin");
        mdIndex.setName("foo.txt");
        logger.info(findMdService.findFileMd(mdIndex).toString());
    }



    private void printDirList(FindDirMdResp findDirMdResp) {
        List<MdAttr> mdAttrs = findDirMdResp.getMdAttrs();
        int k = 0;
        for (MdAttr mdAttr1 : mdAttrs) {
            if (k++ % 10 == 0) {
                System.out.println();
            }
            System.out.print(String.format("[%s,%s]", mdAttr1.getName(), mdAttr1.getSize()));
        }
    }
}
