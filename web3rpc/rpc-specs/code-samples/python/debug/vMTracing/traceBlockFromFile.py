from web3 import Web3
from web3py_ext import extend

host = "https://public-en-kairos.node.kaia.io"

fileName = "/home/sotatek/block.rlp";

w3 = Web3(Web3.HTTPProvider(host))
debug_response = w3.debug.trace_block_from_file(fileName)

print(debug_response)
