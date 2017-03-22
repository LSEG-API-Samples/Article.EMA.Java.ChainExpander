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

import com.thomsonreuters.ema.access.OmmConsumer;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

class FlatChainImpl implements FlatChain, ChainRecordContext, ChainRecordListener
{
    private final OmmConsumer ommConsumer;
    private final String name;
    private final String serviceName;
    private final boolean withUpdates;
    private final SummaryLinksToSkipByDisplayTemplate summaryLinksToSkipByDisplayTemplate;
    private final int nameGuessesCount;
    private final ElementAddedFunction elementAddedFunction;
    private final ElementChangedFunction elementChangedFunction;
    private final ElementRemovedFunction elementRemovedFunction;
    private final ChainCompleteFunction chainCompleteFunction;
    private final ChainErrorFunction chainErrorFunction;    
    private final ChainRecordFactory chainRecordFactory;
    private ChainRecord firstChainRecord;
    private enum State {OPENING, OPENED, CLOSING, CLOSED, IN_ERROR};
    private State state;
    private final Map<Long, String> elementsByPosition = new TreeMap<>();
    

    public FlatChainImpl(FlatChain.Builder builder)
    {
        ommConsumer = builder.ommConsumer;
        name = builder.chainName;
        serviceName = builder.serviceName;
        withUpdates = builder.withUpdates;
        summaryLinksToSkipByDisplayTemplate = builder.summaryLinksToSkipByDisplayTemplate;
        nameGuessesCount = builder.nameGuessesCount;
        elementAddedFunction = builder.elementAddedFunction;
        elementChangedFunction = builder.elementChangedFunction;
        elementRemovedFunction = builder.elementRemovedFunction;
        chainCompleteFunction = builder.chainCompleteFunction;
        chainErrorFunction = builder.chainErrorFunction;

        ChainRecordContext chainRecordContext = this;
        ChainRecordListener chainRecordListener = this;
        chainRecordFactory = new ChainRecordFactoryImpl(chainRecordContext, chainRecordListener);         
        
        state = State.CLOSED;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getServiceName()
    {
        return serviceName;
    }

    @Override
    public synchronized void open()
    {
        if(state == State.OPENED || state == State.OPENING || state == State.IN_ERROR)
            return;
        
        state = State.OPENING;

        firstChainRecord = chainRecordFactory.acquire(getName());
        firstChainRecord.open();
    }

    @Override
    public synchronized void close()
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        state = State.CLOSING;
        elementsByPosition.clear();
        
        firstChainRecord.close();
        firstChainRecord = null;
        
        chainRecordFactory.releaseAll();
    }
    
    @Override
    public synchronized boolean isComplete()
    {
        if(state == State.OPENED || state == State.IN_ERROR)
        {
            return true;
        }
        
        return false;
    }
    
    @Override
    public synchronized boolean isAChain()
    {
        if (firstChainRecord == null)
        {
            return false;
        }
        
        return firstChainRecord.isAChainRecord();
    }
    
    @Override
    public synchronized Map<Long, String> getElements()
    {
        Map<Long, String> deepCopiedElementsToReturn = Collections.unmodifiableMap(elementsByPosition);
        
        return deepCopiedElementsToReturn;
    }
        
    @Override
    public OmmConsumer getOmmConsumer()
    {
        return ommConsumer;
    }

    @Override
    public boolean getWithUpdates()
    {
        return withUpdates;
    }

    @Override
    public SummaryLinksToSkipByDisplayTemplate getSummaryLinksToSkipByDisplayTemplate()
    {
        return summaryLinksToSkipByDisplayTemplate;
    }

    @Override
    public int getNameGuessesCount()
    {
        return nameGuessesCount;
    }
    
    @Override
    public ElementAddedFunction getElementAddedFunction()
    {
        return elementAddedFunction;
    }

    @Override
    public ElementChangedFunction getElementChangedFunction()
    {
        return elementChangedFunction;
    }

    @Override
    public ElementRemovedFunction getElementRemovedFunction()
    {
        return elementRemovedFunction;
    }

    @Override
    public ChainCompleteFunction getChainCompleteFunction()
    {
        return chainCompleteFunction;
    }

    @Override
    public ChainErrorFunction getChainErrorFunction()
    {
        return chainErrorFunction;
    }    

    @Override
    public synchronized void onLinkAdded(long position, String name, ChainRecord chainRecord)
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        elementsByPosition.put(position, name);
        elementAddedFunction.onElementAdded(position, name, this);
    }

    @Override
    public synchronized void onLinkRemoved(long position, ChainRecord chainRecord)
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        elementsByPosition.remove(position);
        elementRemovedFunction.onElementRemoved(position, this);
    }

    @Override
    public synchronized void onLinkChanged(long position, String previousName, String newName, ChainRecord chainRecord)
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        elementsByPosition.replace(position, newName);
        elementChangedFunction.onElementChanged(position, previousName, newName, this);
    }

    @Override
    public synchronized void onCompleted(ChainRecord chainRecord)
    {
        if(state == State.OPENED || state == State.CLOSED || state == State.CLOSING)
            return;

        state = State.OPENED;
        chainCompleteFunction.onComplete(this);
    }

    @Override
    public synchronized void onError(String errorMessage, ChainRecord chainRecord)
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        State previousState = state;
        state = State.IN_ERROR;
        
        chainErrorFunction.onError(errorMessage, this);

        if(previousState == State.OPENING)
        {
            chainCompleteFunction.onComplete(this);
        }
    }
}
