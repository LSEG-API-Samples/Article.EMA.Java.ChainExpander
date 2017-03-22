/*
 * Copyright 2017 Thomson Reuters
 *
 * DISCLAIMER: This source code has been written by Thomson Reuters for the only 
 * purpose of illustrating the "Decoding chains" article published on the 
 * Thomson Reuters Developer Community. It has not been tested for a usage in 
 * production environments.
 *
 * Thomson Reuters Developer Community: https://developers.thomsonreuters.com
 * Decoding chains - Part 1: https://developers.thomsonreuters.com/article/elektron-article-1
 * Decoding chains - Part 2: https://developers.thomsonreuters.com/article/elektron-article-2
 *
 */
package com.thomsonreuters.platformservices.ema.utils.chain;

interface ChainRecordListener
{

    void onLinkAdded(long position, String name, ChainRecord chainRecord);

    void onLinkRemoved(long position, ChainRecord chainRecord);

    void onLinkChanged(long position, String previousName, String newName, ChainRecord chainRecord);

    void onCompleted(ChainRecord chainRecord);

    void onError(String errorMessage, ChainRecord chainRecord);
}
