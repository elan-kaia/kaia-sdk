const { JsonRpcProvider } = require("@klaytn/ethers-ext");
(() => {
let sdk = new JsonRpcProvider("https://public-en-baobab.klaytn.net");

    const transactionHash = '0x5bbcde52084defa9d1c7068a811363cc27a25c80d7e495180964673aa5f47687'

    sdk.eth.getRawTransactionByHash(transactionHash, {}, (err, data, response) => {
        console.log(data);
    });
}
)()