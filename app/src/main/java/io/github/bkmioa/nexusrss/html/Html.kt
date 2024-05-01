package io.github.bkmioa.nexusrss.html

import org.apache.commons.text.StringEscapeUtils

object Html {
    private const val template = """
<html>

<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" type="text/css" href="file:///android_asset/bbcode/xbbcode.css">
  <style>
    body {
      margin: 8px;
    }
    img {
      max-width: 100%;
      height: auto;
    }
    #content {
      word-wrap: break-word;
    }
    @media (prefers-color-scheme: dark) {
      html, img { 
        filter: invert(1) hue-rotate(180deg);
      }
      img {
        opacity: 0.75;    
      }
    }
  </style>
</head>

<body>
  <div id="content"></div>
  <script src="file:///android_asset/bbcode/xbbcode.js"></script>
  <script src="file:///android_asset/markdown/markdown-it.min.js"></script>
  <script>
    var content = '{{content}}'
    console.log(content);
    var result = XBBCODE.process({
      text: content
    });
    
    var md = window.markdownit({ html: true });
    var marked = md.render(result.html);

    console.error("Errors", result.error);
    console.dir(result.errorQueue);
    console.log(result.html);
    document.getElementById("content").innerHTML = marked;
  </script>

</body>

</html>    
"""

    fun render(content: String?): String {
        if (content == null) return ""
        val html = content
            .replace("\r\n\r\n", "\n")
            .replace("\n", "\r\n\r\n")
        return template.replace("{{content}}", StringEscapeUtils.escapeEcmaScript(html))
    }

}