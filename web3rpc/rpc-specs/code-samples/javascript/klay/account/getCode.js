const { JsonRpcProvider } = require("@klaytn/ethers-ext");
(() => {
let sdk = new JsonRpcProvider("https://public-en-baobab.klaytn.net");

    const account = "0xa94f5374fce5edbc8e2a8697c15331677e6ebf0b"
    const blockNumberOrHash = "0x2"
    
    sdk.klay.getCode(account, blockNumberOrHash, {}, (err, data, response) => {
        console.log(data);
    });
}
)()