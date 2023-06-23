package com.dominik;

public class ProgramError extends Error {
    private String m_message;
    public ProgramError(String message)
    {
        m_message = message;
    }
    public void show_error()
    {
        System.out.println(m_message);
    }

}
