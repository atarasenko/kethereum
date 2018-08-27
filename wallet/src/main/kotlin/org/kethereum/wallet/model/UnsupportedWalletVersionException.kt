package org.kethereum.wallet.model

class UnsupportedWalletVersionException
    : Exception("Currently only wallets with version 3 and 4 are supported")