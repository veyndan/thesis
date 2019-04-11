package com.veyndan.thesis

class InsufficientFundsException(requestedFunds: Pennies, currentFunds: Pennies) :
    RuntimeException("Requested $requestedFunds but only have $currentFunds")
