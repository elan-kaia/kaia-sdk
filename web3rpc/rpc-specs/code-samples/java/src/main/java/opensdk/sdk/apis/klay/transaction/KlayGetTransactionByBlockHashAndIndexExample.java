import org.web3j.protocol.kaia.core.method.response.KlayGetTransactionByBlockHashAndIndexResponse;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;

import java.io.IOException;

public class KlayGetTransactionByBlockHashAndIndexExample {
    private Web3j w3 = Web3j.build(new HttpService("https://public-en-kairos.node.kaia.io"));    void klayGetTransactionByBlockHashAndIndexExample() throws IOException {
        KlayGetTransactionByBlockHashAndIndexResponse response = w3.klayGetTransactionByBlockHashAndIndex(
                "0x451cafae98d61b7458b5cef54402830941432278184453e3ca490eb687317e68",
                "0x0"
        ).send();
        response.getResult();
    }
}
