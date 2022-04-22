# PolishTranslator
Swing application with sockets allowing to translate polish words into appropriate ones in another language.

<p align="center">
  <img src=https://user-images.githubusercontent.com/74014874/164570882-4a5478b9-c52a-4a55-985b-f74ff7200eb8.png
   >
</p>

__________________________________________________________________________________________________________________


# How it works

  1. Run program and put words in polish to translate in the input panel.
  2. Choose language into which polish words will be translated.
  3. Get the result in the output panel at the bottom of the application.

<br />
<br />

# General information about the application
  
  The program consists of a client, main server and language servers. Client sends a request to the main server for the translation. 
  Main server sends this information to a specific language server which is responding to the client.

<p align="center">
  <img src=https://user-images.githubusercontent.com/74014874/164575387-14ee7302-9cf5-4150-a189-88c6f41c98dd.png
   >
</p>


__________________________________________________________________________________________________________________
  
## Other information also good to know  
  
  1. The app **does not translate all** polish words, only ones which you chose and put in the **putAvailableLanguages** method in MainServer class.
  3. MainServer initially stores word lists with translations, then sends them to individual languages servers and deletes them from his memory.
  4. Application checks if words and the input language is the database and sends the appropriate message to the user.
  5. In he case if the language server is disconnected, an appropriate message is also returned informing about the resource unavailability.

<p align="center">
  <img src=https://user-images.githubusercontent.com/74014874/164574378-60070fea-f1f4-4930-953f-6bdb5ba20b28.png
   >
</p>

__________________________________________________________________________________________________________________
