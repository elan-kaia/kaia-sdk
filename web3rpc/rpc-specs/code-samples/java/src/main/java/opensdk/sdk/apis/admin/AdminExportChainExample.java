import org.web3j.protocol.kaia.core.method.response.AdminExportChainResponse;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;

import java.io.IOException;

public class AdminExportChainExample {
    private Web3j w3 = Web3j.build(new HttpService("https://public-en-kairos.node.kaia.io"));
    void adminExportChainExample() throws IOException {
        String file = "/tmp/chain3.txt";
        int firstBlock = 1;
        int lastBlock = 500;
        AdminExportChainResponse response = w3.adminExportChain(file, firstBlock, lastBlock).send();
        response.getResult();
    }
}
