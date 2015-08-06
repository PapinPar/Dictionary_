package com.test.papin.dictionary;

public class NewsArchive
{

  private ResponseData responseData;
  private String translatedText;

  public NewsArchive(ResponseData responseData,String text)
  {
    this.responseData = responseData;
    this.translatedText = text;
  }
  public String getText()
  {
    return translatedText;
  }

  public ResponseData getResponseData()
  {
    return responseData;
  }

  class ResponseData
  {
    String translatedText;
   // int match;
    public ResponseData(String Text)
    {
      this.translatedText = Text;

    }
  }


}

