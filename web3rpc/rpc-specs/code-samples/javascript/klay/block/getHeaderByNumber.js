const { JsonRpcProvider } = require("@kaiachain/ethers-ext");
(() => {
  const provider = new JsonRpcProvider("https://public-en-baobab.klaytn.net");

  const blockNumber = 1;

  provider.klay
    .getHeaderByNumber(blockNumber, {}, (err, data, response) => {})
    .then((data) => {
      console.log(data);
    });
})();
