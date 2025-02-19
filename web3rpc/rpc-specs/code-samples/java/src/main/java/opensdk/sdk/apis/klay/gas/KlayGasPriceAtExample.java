//import org.web3j.protocol.kaia.core.method.response.KlayGasPriceAtResponse;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;

import java.io.IOException;

public class KlayGasPriceAtExample {

    private Web3j w3 = Web3j.build(new HttpService("https://public-en-kairos.node.kaia.io"));

    void klayGasPriceAtExample() throws IOException {
        KlayGasPriceAtResponse gr = w3.klayGasPriceAt(
                "0x64")
                .send();
        gr.getResult();
    }
}
