const { JsonRpcProvider } = require("@klaytn/ethers-ext");
(() => {
let sdk = new JsonRpcProvider("https://public-en-baobab.klaytn.net");    const address = '0xa4f42d4d2a3a13874406435500950c9bf2d783db'
    const blockTag = 'latest'
    sdk.klay.accountCreated(address, blockTag, {}, (err, data, response) => {
        console.log(data);
    });
}
)()