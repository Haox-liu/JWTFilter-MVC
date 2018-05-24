package mvc.token.filter;


import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class SignatureHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] bytes;
    private WrapperServletInputStream wrapperServletInputStream;

    public SignatureHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        //读取post请求流的请求参数，保存到bytes
        bytes = IOUtils.toByteArray(request.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        this.wrapperServletInputStream = new WrapperServletInputStream(byteArrayInputStream);

        //post参数重新写入请求流
        reWriteInputStream();
    }

    /**
     * 把参数重新写进请求里
     */
    public void reWriteInputStream() {
        wrapperServletInputStream.setStream(new ByteArrayInputStream(bytes != null ? bytes : new byte[0]));
    }


    @Override
    public ServletInputStream getInputStream() throws IOException {
        return wrapperServletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(wrapperServletInputStream));
    }

    /**
     * 获取post参数
     */
    public String getRequestBody() throws IOException {
        return new String(bytes, "UTF-8");
    }


    private class WrapperServletInputStream extends ServletInputStream {

        private InputStream stream;

        public WrapperServletInputStream(InputStream stream) {
            this.stream = stream;
        }

        public void setStream(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public int read() throws IOException {
            return stream.read();
        }
    }
}
