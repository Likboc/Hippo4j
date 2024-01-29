package com.example.wrapper;

import com.example.Listener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerListenerWrapper {

    private String lastCallMd5;

    private Listener listener;
}
