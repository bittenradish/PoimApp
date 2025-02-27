package com.example.core

import java.io.IOException


class NoConnectivityException(
    override val message: String = "No internet connection"
) : IOException()