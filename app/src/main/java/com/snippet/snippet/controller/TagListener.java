package com.snippet.snippet.controller;

import java.util.List;

/**
 * Generic Listener for when tags are received by ClarifAI
 * @author Jordan Burklund
 */

public interface TagListener {
    void onReceiveTags(List<String> tags);
}
