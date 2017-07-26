package com.speech;


import com.iflytek.cloud.speech.*;
import com.oralcewdp.async.AsyncServlet;
import com.oralcewdp.async.ServletCallAble;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
@WebServlet(value = "/speech",asyncSupported = true)
public class SpeechServlet  extends AsyncServlet<String>{

    @Override
    public long getTimeout() {
        return 5000;
    }

    @Override
    public void init() throws ServletException {
        SpeechUtility.createUtility( SpeechConstant.APPID +"=59785f6b");
    }

    @Override
    public void onTimeout(AsyncContext asyncContext, ServletCallAble<String> callAble) throws IOException {
        HttpServletResponse resp = callAble.getResponse();
        resp.setStatus(500);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("系统timeout！");
    }

    @Override
    public void onComplete(AsyncContext asyncContext, ServletCallAble<String> callAble, String result) throws IOException {
        HttpServletResponse resp = callAble.getResponse();
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(result);
    }

    @Override
    public void onError(AsyncContext asyncContext, ServletCallAble<String> callAble, Throwable throwable) throws IOException {
        throwable.printStackTrace();
        HttpServletResponse resp = callAble.getResponse();
        resp.setStatus(500);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("系统错误！");
    }

    @Override
    public boolean doAsync(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String text = req.getParameter("text");
        if(text == null || text.isEmpty()){
            resp.setStatus(500);
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print("参数错误！");
            return false;
        }
        return true;
    }

    @Override
    public ServletCallAble<String> getCallable() {
        return new ServletCallAble<String>() {
            @Override
            public String call() throws Exception {

                String text = getRequest().getParameter("text");

                SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer( );
                mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
                mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速，范围0~100
                mTts.setParameter(SpeechConstant.PITCH, "50");//设置语调，范围0~100
                mTts.setParameter(SpeechConstant.VOLUME, "50");//设置音量，范围0~100

                String res[] = new String[1];
                mTts.synthesizeToUri(text, "./" + UUID.randomUUID() + ".pcm", new SynthesizeToUriListener() {
                    @Override
                    public void onBufferProgress(int i) {

                    }

                    @Override
                    public void onSynthesizeCompleted(String s, SpeechError speechError) {
                        if(speechError != null){
                            res[0] = speechError.getErrorDesc();
                        }else{
                            res[0] = s;
                        }
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, int i3, Object o, Object o1) {

                    }
                });
                while (res[0] == null);
                return res[0];
            }
        };
    }
}
