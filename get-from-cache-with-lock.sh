#!/bin/bash

ab -t 60 -c 100 http://127.0.0.1:8080/api/cache/probabilistic-value-with-lock?cacheKey=Vasyl