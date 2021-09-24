import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClient;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Function implements RequestStreamHandler
{
    private JSONParser jsonParser = new JSONParser();

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException
    {
        String response;

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try
        {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(bufferedReader);

            // read the text from json Object

            if (jsonObject.get("text") != null)
            {
                String text = (String) jsonObject.get("text");

                if (text != null && !text.equals(""))
                {
                    // send the text to comprehend to get the results

                    AmazonComprehend amazonComprehend = getAmazonComprehendClient();

                    DetectKeyPhrasesRequest request = new DetectKeyPhrasesRequest().withText(text).withLanguageCode("en");

                    DetectKeyPhrasesResult result = amazonComprehend.detectKeyPhrases(request);

                    if (result.getKeyPhrases().size() <= 0)
                    {

                        JSONObject jsonResponse = getJsonResponse("Any key phrases not found");

                        response = jsonResponse.toJSONString();
                    }
                    else
                    {
                        StringBuilder stringBuilder = new StringBuilder();

                        for (int i=0; i<result.getKeyPhrases().size(); i++)
                        {
                            if (i == result.getKeyPhrases().size() - 1)
                            {
                                stringBuilder.append(i + 1).append(". ").append(result.getKeyPhrases().get(i).getText()).append(".\n");
                            }
                            else
                            {
                                stringBuilder.append(i + 1).append(". ").append(result.getKeyPhrases().get(i).getText()).append(",\n");
                            }
                        }

                        JSONObject jsonResponse = getJsonResponse(stringBuilder.toString());

                        response = jsonResponse.toJSONString();
                    }
                }
                else
                {
                    JSONObject jsonResponse = getJsonResponse("Any key phrases not found");

                    response = jsonResponse.toJSONString();
                }
            }
            else
            {
                JSONObject jsonResponse = getJsonResponse("Any key phrases not found");

                response = jsonResponse.toJSONString();
            }
        }
        catch (ParseException e)
        {
            JSONObject jsonResponse = getJsonResponse("Any key phrases not found");

            response = jsonResponse.toJSONString();
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, UTF_8);
        outputStreamWriter.write(response);
        outputStreamWriter.close();
    }

    private JSONObject getJsonResponse(String result)
    {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("text",result);

        return jsonObject;
    }

    // create a client for the amazon comprehend

    private AmazonComprehend getAmazonComprehendClient()
    {
        return AmazonComprehendClient.builder().withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials()
        {
            public String getAWSAccessKeyId()
            {
                return "AKIAINP5QUYC67NNSFUA";
            }

            public String getAWSSecretKey()
            {
                return "CfmsXdlwrsrsqqkAuEQCOP8/CgEhkAfiTIhOnrpJ";
            }
        })).build();
    }
}
