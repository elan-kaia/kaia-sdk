import org.web3j.protocol.kaia.core.method.response.KaiaGetChainConfigResponse;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;

import java.io.IOException;

public class KaiaGetChainConfigExample {
    private Web3j w3 = Web3j.build(new HttpService("https://public-en-kairos.node.kaia.io"));    void kaiaGetChainConfigExample() throws IOException {
        Integer blogNumberOrTag = 100;
        KaiaGetChainConfigResponse response = w3.kaiaGetChainConfig(blogNumberOrTag).send();
        response.getResult();
    }
}
