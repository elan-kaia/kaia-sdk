const { JsonRpcProvider } = require("@klaytn/ethers-ext");
(() => {
let sdk = new JsonRpcProvider("https://public-en-baobab.klaytn.net");
    const blockHash = '0x4c4cbf242a80183d2ea2daf047c578d5fc89c0b14c4262606c8b6bb0b36715be'
    const index = '0x0'

    sdk.eth.getRawTransactionByBlockHashAndIndex(blockHash, index, {}, (err, data, response) => {
        console.log(data);
    });

}
)()