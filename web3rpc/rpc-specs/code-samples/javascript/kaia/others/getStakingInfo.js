const { JsonRpcProvider } = require("@kaiachain/ethers-ext");
(() => {
  const provider = new JsonRpcProvider("https://public-en-baobab.klaytn.net");

  const blockNumberOrTag = "latest";

  provider.kaia
    .getStakingInfo(blockNumberOrTag, {}, (err, data, response) => {})
    .then((data) => {
      console.log(data);
    });
})();
