const { JsonRpcProvider } = require("@klaytn/ethers-ext");
(() => {
let sdk = new JsonRpcProvider("https://public-en-baobab.klaytn.net");    
    const txHash = "0x344fc43b5b87984d5a50fe2c54e121f94945ba9ff9da20f9de0f1b4914f47055";

    sdk.debug.traceTransaction(txHash, {}, (err, data, response) => {
        console.log(data);
    });
}
)()