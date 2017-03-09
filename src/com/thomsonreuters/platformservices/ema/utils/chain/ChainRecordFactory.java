package com.thomsonreuters.platformservices.ema.utils.chain;

interface ChainRecordFactory
{

    ChainRecord acquire(String name);

    void release(ChainRecord toRelease);
}
