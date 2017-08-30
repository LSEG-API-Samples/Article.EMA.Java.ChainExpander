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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class RecursiveChainImpl implements RecursiveChain
{
    private final OmmConsumer ommConsumer;
    private final String name;
    private final String serviceName;
    private final SummaryLinksToSkipByDisplayTemplate summaryLinksToSkipByDisplayTemplate;
    private final int nameGuessesCount;
    private final int maxDepth;
    private final ElementAddedFunction elementAddedFunction;
    private final ChainCompleteFunction chainCompleteFunction;
    private final ChainErrorFunction chainErrorFunction;
    private enum State {OPENING, OPENED, CLOSING, CLOSED, IN_ERROR};
    private State state;
    private final FlatChain currentDepthChain;
    private final Map<RecursiveChain, Long> positionsBySubChain = new HashMap<>();
    private final boolean maxDepthReached;
    private boolean isComplete;

    public RecursiveChainImpl(RecursiveChain.Builder builder)
    {
        ommConsumer = builder.ommConsumer;
        name = builder.chainName;
        serviceName = builder.serviceName;
        summaryLinksToSkipByDisplayTemplate = builder.summaryLinksToSkipByDisplayTemplate;
        nameGuessesCount = builder.nameGuessesCount;
        maxDepth = builder.maxDepth;
        elementAddedFunction = builder.elementAddedFunction;
        chainCompleteFunction = builder.chainCompleteFunction;
        chainErrorFunction = builder.chainErrorFunction;
        
        state = State.CLOSED;
        currentDepthChain = buildFlatChain(name);
        if(maxDepth == 0)
            maxDepthReached = true;
        else
            maxDepthReached = false;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getServiceName()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public synchronized void open()
    {
        if(state == State.OPENED || state == State.OPENING || state == State.IN_ERROR)
            return;
        
        state = State.OPENING;
        
        if(maxDepthReached)
        {
            state = State.IN_ERROR;
            chainErrorFunction.onError("MaxDepth reached, sub-chain <" + name + "> will not be opened.", this);
            chainCompleteFunction.onComplete(this);
            return;
        }
        
        currentDepthChain.open();
    }

    @Override
    public synchronized void close()
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        state = State.CLOSING;
        
        closeSubChains();
        currentDepthChain.close();        
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
    public boolean isAChain()
    {
        if(maxDepthReached)
        {
            return false;
        }
        
        return currentDepthChain.isAChain();
    }
    
    @Override
    public synchronized Map<List<Long>, List<String>> getElements()
    {
        Comparator<List<Long>> positionComparator = buildElementPositionComparator();
        Map<List<Long>, List<String>> allElementsByPosition = new TreeMap<>(positionComparator);
        
        for (Map.Entry<RecursiveChain, Long> entry : positionsBySubChain.entrySet())    
        {
            Long subChainPosition = entry.getValue();
            RecursiveChain subChain = entry.getKey();
            
            if(subChain.isAChain())
            {
                for (Map.Entry<List<Long>, List<String>> subChainElementEntry : subChain.getElements().entrySet())
                {
                    List<Long> elementPositionInSubChain = subChainElementEntry.getKey();
                    List<Long> elementPositionInChain = buildElementPositionInChain(elementPositionInSubChain, subChainPosition);
                    List<String> elementNameInSubChain = subChainElementEntry.getValue();
                    List<String> elementNameInChain = buildElementNameInChain(elementNameInSubChain, subChain);
                    allElementsByPosition.put(elementPositionInChain, elementNameInChain);
                }
            }
            else
            {
                List<Long> elementPositionInChain = buildElementPositionInChain(subChainPosition);
                List<String> elementNameSubChain = buildElementNameInChain(subChain);
                allElementsByPosition.put(elementPositionInChain, elementNameSubChain);
            }
        }
               
        return allElementsByPosition;
    }
        
    private Comparator<List<Long>> buildElementPositionComparator()
    {
        return new Comparator<List<Long>>() 
        {
            @Override public int compare(List<Long> key1, List<Long> key2) 
            {
                Iterator<Long> key1Iterator = key1.iterator();
                Iterator<Long> key2Iterator = key2.iterator();
                while(key1Iterator.hasNext() && key2Iterator.hasNext())
                {
                    long key1Value = key1Iterator.next();
                    long key2Value = key2Iterator.next();
                    
                    if(key1Value != key2Value)
                    {
                        return (int) (key1Value - key2Value);
                    }
                }
                
                return key1.size() - key1.size();
            }           
        };
    }
    
    private int computeNextMaxDepth()
    {
        int nextMaxDepth = -1;
        
        if(maxDepth > 0)
        {
            nextMaxDepth = maxDepth - 1;
        }
        
        return nextMaxDepth;
    }
    
    private FlatChain buildFlatChain(String chainName)
    {
        FlatChain newChain = new FlatChain.Builder()
                    .withOmmConsumer(ommConsumer)
                    .withChainName(chainName)
                    .withServiceName(serviceName)
                    .withUpdates(false)
                    .withSummaryLinksToSkip(summaryLinksToSkipByDisplayTemplate)
                    .withNameGuessingOptimization(nameGuessesCount)
                    .onElementAdded (
                        new FlatChain.ElementAddedFunction() {
                            @Override
                            public void onElementAdded(long position, String name, FlatChain chain) {
                                onLinkAdded(position, name, chain);
                            }
                        }
                    )
                    .onChainComplete(
                        new FlatChain.ChainCompleteFunction() {
                            @Override
                            public void onComplete(FlatChain chain) {
                                RecursiveChainImpl.this.onComplete(chain);
                            }
                        }
                    )
                    .onChainError(
                        new FlatChain.ChainErrorFunction() {
                            @Override
                            public void onError(String errorMessage, FlatChain chain) {
                                RecursiveChainImpl.this.onError(errorMessage, chain);
                            }
                        }
                    )
                    .build();      
        
        return newChain;
    }
    
    private RecursiveChain buildRecursiveChain(String chainName)
    {
        RecursiveChain subChain = new RecursiveChain.Builder()
                    .withOmmConsumer(ommConsumer)
                    .withChainName(chainName)
                    .withServiceName(serviceName)
                    .withSummaryLinksToSkip(summaryLinksToSkipByDisplayTemplate)
                    .withMaxDepth(computeNextMaxDepth())
                    .onElementAdded(
                        new ElementAddedFunction() {
                            @Override
                            public void onElementAdded(List<Long> position, List<String> name, RecursiveChain chain) {
                                onSubLinkAdded(position, name, chain);
                            }
                        }
                    )
                    .onChainComplete(
                        new ChainCompleteFunction() {
                            @Override
                            public void onComplete(RecursiveChain chain) {
                                onSubChainComplete(chain);
                            }
                        }
                    )
                    .onChainError(
                        new ChainErrorFunction() {
                            @Override
                            public void onError(String errorMessage, RecursiveChain chain) {
                                onSubChainError(errorMessage, chain);
                            }
                        }
                    )
                    .build();

        return subChain;
    }
    
    private void openSubChain(String chainName, long position)
    {
        RecursiveChain subChain = buildRecursiveChain(chainName);
        positionsBySubChain.put(subChain, position);
        subChain.open();
    }

    private void closeSubChains()
    {
        Collection<RecursiveChain> subChains = positionsBySubChain.keySet();
        for(RecursiveChain subChain : subChains)
        {
            subChain.close();
        }
        positionsBySubChain.clear();
    }
        
    private void onLinkAdded(long linkPosition, String linkName, FlatChain chain)
    {
        if(linkName.isEmpty())
        {
            elementAddedFunction.onElementAdded(
                    buildElementPositionInChain(linkPosition),
                    buildElementNameInChain(linkName), 
                    this);            
        }
        else
        {
            openSubChain(linkName, linkPosition);        
        }
    }

    private void onComplete(FlatChain chain)
    {
        checkIfCompleteAndNotify();
    }
    
    private void onError(String errorMessage, FlatChain chain)
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        state = State.IN_ERROR;
        chainErrorFunction.onError(errorMessage, this);
    }
    
    private void onSubLinkAdded(List<Long> linkPositionInSubChain, List<String> linkNameInSubChain, RecursiveChain subChain)
    {
        elementAddedFunction.onElementAdded(
                buildElementPositionInChain(linkPositionInSubChain, subChain),
                buildElementNameInChain(linkNameInSubChain, subChain), 
                this);
    }
    
    private void onSubChainComplete(RecursiveChain subChain)
    {
        checkIfCompleteAndNotify();
    }

    private void onSubChainError(String errorMessage, RecursiveChain subChain)
    {
        if(state == State.CLOSED || state == State.CLOSING)
            return;

        if(!subChain.isAChain())
        {
            
            elementAddedFunction.onElementAdded(
                    buildElementPositionInChain(subChain), 
                    buildElementNameInChain(subChain),
                    this);
        }
        else
        {
            chainErrorFunction.onError(errorMessage, this);
        }
    }

    private void checkIfCompleteAndNotify()
    {
        if (state != State.OPENING && state != State.IN_ERROR)
        {
            return;
        }

        if(!currentDepthChain.isComplete())
        {
            return;
        }

        isComplete = true;
        Collection<RecursiveChain> subChains = positionsBySubChain.keySet();
        for(RecursiveChain subChain : subChains)
        {
            if(!subChain.isComplete())
            {
                isComplete = false;
            }
            else
            {
            }
        }
        
        if(isComplete)
        {
            state = State.OPENED;
            chainCompleteFunction.onComplete(this);
        }
        else
        {
        }
    }    
    
    private List<Long> buildElementPositionInChain(List<Long> elementPositionInSubChain, RecursiveChain subChain)
    {
        LinkedList<Long> elementPositionInChain = new LinkedList<>(elementPositionInSubChain);
        long subChainPosition = positionsBySubChain.get(subChain);
        elementPositionInChain.addFirst(subChainPosition);
        return elementPositionInChain;
    }    

    private List<Long> buildElementPositionInChain(List<Long> elementPositionInSubChain, long subChainPosition)
    {
        LinkedList<Long> elementPositionInChain = new LinkedList<>(elementPositionInSubChain);
        elementPositionInChain.addFirst(subChainPosition);        
        return elementPositionInChain;
    }    
    
    private List<Long> buildElementPositionInChain(RecursiveChain subChain)
    {
        LinkedList<Long> elementPositionInChain = new LinkedList<>();
        long elementPosition = positionsBySubChain.get(subChain);
        elementPositionInChain.add(elementPosition);
        return elementPositionInChain;
    }    
    
    private List<Long> buildElementPositionInChain(long position)
    {
        LinkedList<Long> elementPositionInChain = new LinkedList<>();
        elementPositionInChain.add(position);
        return elementPositionInChain;
    }    
    
    private List<String> buildElementNameInChain(String elementName)
    {
        LinkedList<String> elementNameInChain = new LinkedList<>();
        elementNameInChain.add(this.name);
        elementNameInChain.add(elementName);
        return elementNameInChain;
        
    }

    private List<String> buildElementNameInChain(List<String> elementNameInSubChain, RecursiveChain subChain)
    {
        LinkedList<String> elementNameInChain = new LinkedList<>(elementNameInSubChain);        
        elementNameInChain.addFirst(subChain.getName());
        return elementNameInChain;
        
    }

    private List<String> buildElementNameInChain(RecursiveChain subChain)
    {
        LinkedList<String> elementNameInChain = new LinkedList<>();
        elementNameInChain.add(subChain.getName());
        return elementNameInChain;        
    }    
}
