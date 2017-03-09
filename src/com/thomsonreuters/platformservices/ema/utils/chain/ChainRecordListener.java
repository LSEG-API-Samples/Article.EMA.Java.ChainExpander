package com.thomsonreuters.platformservices.ema.utils.chain;

interface ChainRecordListener
{

    void onLinkAdded(long position, String name, ChainRecord chainRecord);

    void onLinkRemoved(long position, ChainRecord chainRecord);

    void onLinkChanged(long position, String previousName, String newName, ChainRecord chainRecord);

    void onCompleted(ChainRecord chainRecord);

    void onError(String errorMessage, ChainRecord chainRecord);
}
