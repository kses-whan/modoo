package com.icure.kses.modoo.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.icure.kses.modoo.log.Log4jHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Modoo_HttpClient implements Modoo_HttpParams {

    private static Log4jHelper logger = Log4jHelper.getInstance();

    public final String APPLICATION_JSON = "application/json";
    public final String MULTIPART_FORMDATA = "multipart/form-data";
    public final String USER_AGENT = "KSES-Android-App";
    public final String HTTPS_SCHEMA = "https://";

    private static final String LINE_FEED = "\r\n";
    private static final String CHARSET = "UTF-8";
    private static final String TWO_HYPHEN = "--";
    private final static String BOURDARY = String.format("%x", new Random().hashCode());

    public static final int MultiPart = 0;
    public static final int FormUrl = 1;
    public static final int GET = 2;
    public static final int JsonBody = 5;

    public static final int HTTP_CONNECTION_TIMEOUT_BASE = 10000;
    public static final int HTTP_CONNECTION_TIMEOUT_EACH_FILE = 60000;

    public int encodeType;
    public String referer = "";
    private String targetURL = null;

    private HashMap hashmap = new HashMap();
    private String jsonBody;
    private String authHeader;

    private Context context;

    private boolean occurredException = false;

    // for POST
    private OutputStream outputStream;
    private PrintWriter writer;
    private FileInputStream fis = null;

    public Modoo_HttpClient() {
        //
    }

    public void init(Context context, String url) {
        this.context = context;
        this.targetURL = url;
        this.encodeType = FormUrl;
    }

    public void init(Context context, String url, int encodeType) {
        this.context = context;
        this.targetURL = url;
        this.encodeType = encodeType;
    }

    public String connectToServerGet() {
        String result = "";

        try {
            logger.info("KSES_HttpClient - connectToServerGet - targetURL : " + targetURL);

            URL url = new URL(targetURL);

            HttpURLConnection connection = getHttpUrlConnection(url);
            if (connection != null) {
                if (0 >= connectTimeoutMills) {
                    connectTimeoutMills = HTTP_CONNECTION_TIMEOUT_BASE;
                    socketTimeoutMills = HTTP_CONNECTION_TIMEOUT_BASE;
                }

                connection.setConnectTimeout(connectTimeoutMills * 5);
                connection.setReadTimeout(connectTimeoutMills * 5);
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK ||
                        connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    result = IOUtils.toString(connection.getInputStream(), CHARSET);
                }

                logger.info("KSES_HttpClient - connectToServerGet - ResponseCode : " + connection.getResponseCode());

                connection.disconnect();
            }
        } catch (Exception e) {
            logger.error("KSES_HttpClient - connectToServerGet - ERROR : ", e);
            setOccurredException(true);
            result = null;

            return result;
        }
        return result;
    }

    public String connectToServer() {
        return connectToServer("POST");
    }

    public long getResonseTickCount(long tickCount) {
        return System.currentTimeMillis() - tickCount;
    }

    private void writeReponseLog(String message, long lastTickCount) {
        long MAX_WARNING_RESPONSE_TICKCOUNT = 100;
        long responseTickCount = getResonseTickCount(lastTickCount);
        if (responseTickCount > MAX_WARNING_RESPONSE_TICKCOUNT) {
            logger.info(message + " : [WARN] Response TickCount=" + responseTickCount);
        } else {
            logger.info(message + " : Response TickCount=" + responseTickCount);
        }
    }

    private static long connectToServerConnectionID = 0;    // 연결 ID 값

    public String connectToServer(String httpMethod) {
        long lTickCount = System.currentTimeMillis();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA);

        HttpURLConnection connection = null;
        String result = "";

        File file = null;
        connectToServerConnectionID = connectToServerConnectionID + 1;
        long connectionID = connectToServerConnectionID;

        try {
            logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], targetURL : " + targetURL);
            URL url = new URL(targetURL);

            connection = getHttpUrlConnection(url);

            if (connection != null) {

                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestMethod(httpMethod);

                connection.setUseCaches(false);
                connection.setDoOutput(true); // indicates POST method
                connection.setDoInput(true);

                int fileCount = 0;

                if (encodeType == MultiPart) {
                    logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], encodeType == MultiPart : " + targetURL);

                    MultipartData multipartData = generateMultipartStringBuilder(hashmap);
                    String closingContents = "";
                    // 캐쉬를 사용하지 않음
                    connection.setUseCaches(false);
                    // HTTP스트리밍을 유효화함
                    connection.setChunkedStreamingMode(0);
                    // 접속 유지 설정
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    // 사용자 에이전트 설정
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    // POST데이터 형식 설정 (Multipart)
                    connection.setRequestProperty("Content-Type", String.format("multipart/form-data; boundary=%s", BOURDARY));
                    // POST데이터 길이 설정
                    connection.setRequestProperty("Content-Length", String.valueOf(multipartData.getiContentsLength()));

                    if (multipartData.getFile() != null)
                        fileCount++;

                    connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT_BASE * 6);
                    connection.setReadTimeout(HTTP_CONNECTION_TIMEOUT_BASE + (fileCount * HTTP_CONNECTION_TIMEOUT_EACH_FILE));

                    // 데이터 보내기
                    outputStream = new DataOutputStream(connection.getOutputStream());
                    ((DataOutputStream) outputStream).writeUTF(multipartData.getContentsBuilder().toString());
                    logger.info("contentsBuilder - : CID[" + connectionID + "], " + multipartData.getContentsBuilder().toString());

                    // 파일 전송
                    file = multipartData.getFile();
                    if (file != null) {
                        try {
                            fis = FileUtils.openInputStream(file);
                            int copyResult = IOUtils.copy(fis, outputStream);
                            logger.info("KSES_HttpClient - IOUtils.copy : CID[" + connectionID + "], " + copyResult);
                        } catch (Exception e) {
                            String currentTime = formatter.format(new Date());
                            logger.error("KSES_HttpClient - IOUtils.copy inner CID[" + connectionID + "], ERROR : " + currentTime + " , ", e);
                        }
                    } else {
                        logger.error("IOUtils.copy CID[" + connectionID + "], ERROR : file is NULL");
                    }

                    closingContents = String.format("%s%s%s%s%s", LINE_FEED, TWO_HYPHEN, BOURDARY, TWO_HYPHEN, LINE_FEED);
                    ((DataOutputStream) outputStream).writeBytes(closingContents);

                } else if (encodeType == JsonBody) {
                    logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], encodeType == JsonBody : " + targetURL);

                    connection.setRequestProperty("Content-Type", "application/json; UTF-8");
                    if (authHeader != null && !authHeader.isEmpty()) {
                        connection.setRequestProperty("Authorization", authHeader);
                    }
                    connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT_BASE * 6);
                    connection.setReadTimeout(HTTP_CONNECTION_TIMEOUT_BASE + (fileCount * HTTP_CONNECTION_TIMEOUT_EACH_FILE));

                    outputStream = connection.getOutputStream();
                    writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);

                    logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], Authorization : " + authHeader);
                    logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], jsonbody : " + jsonBody);
                    writer.print(jsonBody);
                    writer.close();

                } else {
                    logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], encodeType == POST : " + targetURL);

                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT_BASE * 6);
                    connection.setReadTimeout(HTTP_CONNECTION_TIMEOUT_BASE + (fileCount * HTTP_CONNECTION_TIMEOUT_EACH_FILE));
                    outputStream = connection.getOutputStream();
                    writer = new PrintWriter(new OutputStreamWriter(outputStream, CHARSET), true);
                    String postParameters = createQueryStringForParameters(hashmap);
                    writer.print(postParameters);
                    writer.close();
                }

                int responseCode = connection.getResponseCode();
                logger.info("KSES_HttpClient - connectToServer - CID[" + connectionID + "], responseCode : " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), CHARSET);
                } else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    result = IOUtils.toString(connection.getErrorStream(), CHARSET);
                }

                if (fis != null)
                    fis.close();

            } else {
                logger.error("connectToServer CID[" + connectionID + "], ERROR : connection is null");
            }
        } catch (Exception e) {
            setOccurredException(true);
            logger.error("KSES_HttpClient - connectToServer CID[" + connectionID + "], ERROR : ", e);
            result = null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                    outputStream = null;
                }

                if (connection != null) {
                    connection.disconnect();
                }

            } catch (Exception e) {
                logger.error("KSES_HttpClient - connectToServer CID[" + connectionID + "], ERROR : ", e);
            }
            return result;
        }
    }

    public class MultipartData {
        private StringBuilder contentsBuilder = new StringBuilder();
        private int iContentsLength = 0;
        private File file;

        /**
         * @return the contentsBuilder
         */
        public StringBuilder getContentsBuilder() {
            return contentsBuilder;
        }

        /**
         * @param contentsBuilder the contentsBuilder to set
         */
        public void setContentsBuilder(StringBuilder contentsBuilder) {
            this.contentsBuilder = contentsBuilder;
        }

        /**
         * @return the iContentsLength
         */
        public int getiContentsLength() {
            return iContentsLength;
        }

        /**
         * @param iContentsLength the iContentsLength to set
         */
        public void setiContentsLength(int iContentsLength) {
            this.iContentsLength = iContentsLength;
        }

        /**
         * @return the file
         */
        public File getFile() {
            return file;
        }

        /**
         * @param file the file to set
         */
        public void setFile(File file) {
            this.file = file;
        }
    }

    public MultipartData generateMultipartStringBuilder(HashMap<String, Object> postData) throws Exception {

        StringBuilder contentsBuilder = new StringBuilder();
        String closingContents = "";
        int iContentsLength = 0;
        String fileTagName = "";
        String filePath = "";
        File file = null;

        contentsBuilder.append(LINE_FEED);

        for (Map.Entry<String, Object> data : postData.entrySet()) {
            String key = data.getKey();
            String val;

            if (!key.equals("File")) {
                val = (String) data.getValue();
                contentsBuilder.append(String.format("%s%s%s", TWO_HYPHEN, BOURDARY, LINE_FEED));
                contentsBuilder.append(String.format("Content-Disposition: form-data; name=\"%s\"%s", key, LINE_FEED));
                contentsBuilder.append(LINE_FEED);
                contentsBuilder.append(val);
                contentsBuilder.append(LINE_FEED);
            } else {
                fileTagName = key;

                File realfile = (File) data.getValue();
                file = realfile;
                filePath = file.getPath();
            }
        }

        // 맨 마지막에 파일정보를 기록
        contentsBuilder.append(String.format("%s%s%s", TWO_HYPHEN, BOURDARY, LINE_FEED));
        contentsBuilder.append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"%s", fileTagName, filePath, LINE_FEED));

        // 파일이 있을 경우에는 contents의 길이에 파일 크기도 포함해야함
        if (file != null) {
            // 파일의 크기 추가
            iContentsLength += file.length();

            // MIME 취득
            int iExtPos = filePath.lastIndexOf(".");
            String ext = (iExtPos > 0) ? filePath.substring(iExtPos + 1) : "";
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());

            contentsBuilder.append(String.format("Content-Type: %s%s", mime, LINE_FEED));
        }
        // 파일이 없을 경우
        else {
            contentsBuilder.append(String.format("Content-Type: application/octet-stream%s", LINE_FEED));
        }

        contentsBuilder.append(LINE_FEED);
        closingContents = String.format("%s%s%s%s%s", LINE_FEED, TWO_HYPHEN, BOURDARY, TWO_HYPHEN, LINE_FEED);

        iContentsLength += contentsBuilder.toString().getBytes(CHARSET).length;
        iContentsLength += closingContents.getBytes(CHARSET).length;

        MultipartData multipartData = new MultipartData();
        multipartData.setContentsBuilder(contentsBuilder);
        multipartData.setiContentsLength(iContentsLength);
        if (file != null) multipartData.setFile(file);

        return multipartData;
    }

    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    public static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;

            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(URLEncoder.encode(
                                parameters.get(parameterName)));

                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }


    @Override
    public void pushDataKey(String key, String value) {
        hashmap.put(key, value);
    }


    @Override
    public void pushFileKey(String key, File value) {
        hashmap.put(key, value);
    }


    @Override
    public void pushImageKey(String key, Bitmap value) {
        hashmap.put(key, value);
    }

    @Override
    public void addJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }

    @Override
    public void addAuthorizationHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public boolean isOccurredException() {
        return occurredException;
    }

    public void setOccurredException(boolean occurredException) {
        this.occurredException = occurredException;
    }

    private HttpURLConnection getHttpUrlConnection(URL url) throws IOException {
        HttpURLConnection connection = null;
        if (url.getProtocol().toLowerCase().equals("https")) {
            trustAllHosts();
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            connection = httpsURLConnection;
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        return connection;
    }

    public static String dumpCerts(java.security.cert.X509Certificate[] certs) {
        String ret = "";
        java.security.cert.X509Certificate cert = null;

        for (int i = 0; i < certs.length; i++) {
            cert = certs[i];
            if (i == 0) {
                ret = "Total=" + certs.length;
            } else {
                ret = ", ";
            }
            ret = cert.toString();
        }

        return ret;
    }

    private static boolean disableTruestAllHosts = false;    // trustAllHosts 가 인증 관련 기능을 무효화할지 여부(사설 인증서도 사용 가능해짐)
    private static boolean isCallOnlyOneForTrustAllHosts = true;    // trustAllHosts 를 한번만 호출할 것인지 여부
    // 근거 => TITLE : HttpsURLConnection.setDefaultSSLSocketFactory() 는 최초 1회만 호출되면 됨
    // HttpsURLConnection.setDefaultSSLSocketFactory() 함수를 매번 호출할 필요 없이 1회만 호출하면 됨.
    // HttpsURLConnection.setDefaultSSLSocketFactory() 함수를 여러 번 호출하는 경우 "SSL handshake timed out" Exception 이 발생한 것일지도 모르겠음.
    private static boolean isCalledForTrustAllHosts = false;    // trustAllHosts 가 호출됐는지 여부

    private static void trustAllHosts() {
        if (isCallOnlyOneForTrustAllHosts == true) {
            if (isCalledForTrustAllHosts == true) {
                return;
            }
        }
        isCalledForTrustAllHosts = true;
        if (disableTruestAllHosts == true) {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                }
            };

            SSLContext sc = null;
            try {
                sc = SSLContext.getInstance("SSL");
            } catch (NoSuchAlgorithmException e) {
                logger.error("trustAllHosts - SSLContext.getInstance Exception=" + e.getMessage());
            }
            try {
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                logger.error("trustAllHosts - getAcceptedIssuers Exception=" + e.getMessage());
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            logger.info("trustAllHosts - HttpsURLConnection.setDefaultSSLSocketFactory() is called.");
        } else {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {}

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType)
                        throws java.security.cert.CertificateException {}
            }};

            try {
                logger.info("trustAllHosts - setDefaultSSLSocketFactory Start");
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (Exception e) {
                logger.error("KSES_HttpClient - trustAllHosts - ERROR : ", e);
            }
        }
    }

    private void createDumpFile(String time, File file) {
        if (time == null) {
            logger.error("KSES_HttpClient - createDumpFile() - ERROR  : time is NULL");
            return;
        }

        if (file == null) {
            logger.error("KSES_HttpClient - createDumpFile() - ERROR  : file is NULL");
            return;
        }

        logger.info("KSES_HttpClient - createDumpFile() : " + time + " , " + file.getAbsolutePath());

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            String fileSuffix = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));

            File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartLog");
            if (!saveFile.exists()) {
                saveFile.mkdir();
            }
            saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartLog/dump");
            if (!saveFile.exists()) {
                saveFile.mkdir();
            }
            saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartLog/dump/" + "dump_" + time + fileSuffix);
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }

            fis = new FileInputStream(file.getAbsolutePath());
            fos = new FileOutputStream(saveFile);

            in = fis.getChannel();
            out = fos.getChannel();

            in.transferTo(0, in.size(), out);

        } catch (Exception e) {
            logger.error("KSES_HttpClient - createDumpFile() - ERROR " + e.getMessage());
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (fos != null)
                    fos.close();
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (IOException e) {
                logger.error("KSES_HttpClient - createDumpFile() - IO ERROR " + e.getMessage());
            }
        }
    }

    int connectTimeoutMills = 0;
    int socketTimeoutMills = 0;

    public void setHttpConnectionTimeout(int timeoutMills) {
        this.connectTimeoutMills = timeoutMills;
        this.socketTimeoutMills = timeoutMills;
    }
}
