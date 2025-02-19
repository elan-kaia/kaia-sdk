import org.web3j.protocol.kaia.core.method.response.KlayFeeHistoryResponse;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;

import java.io.IOException;
import java.util.List;

public class KlayFeeHistoryExample {

    private Web3j w3 = Web3j.build(new HttpService("https://public-en-kairos.node.kaia.io"));
    void klayFeeHistoryExample() throws IOException {
        KlayFeeHistoryResponse fr = w3.klayFeeHistory(
            "0x10",
            "latest",
            List.of(0.1, 0.2, 0.3))
        .send();
        fr.getResult();
    }
}
