import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.kaia.Web3j;
import org.web3j.protocol.kaia.core.method.response.KlayGetActiveAddressFromRegistryResponse;

import java.io.IOException;

public class KlayGetActiveAddressFromRegistryExample {
    private Web3j w3 = Web3j.build(new HttpService("https://public-en-kairos.node.kaia.io"));

    void klayGetActiveAddressFromRegistry() throws IOException {
        String blockNumber = "latest";
        KlayGetActiveAddressFromRegistryResponse response = w3.klayGetActiveAddressFromRegistry("kip113", blockNumber)
                .send();
        response.getResult();
    }
}
