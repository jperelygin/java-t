import java.util.Scanner;

public class ArgumentParser {

  public static void main(String[] args) {

      Word wordToTranslate;

      if (args.length < 1){
          System.out.println("Enter the word to translate to russian: ");
          Scanner input = new Scanner(System.in);
          String inputWord = input.nextLine();
          wordToTranslate = new Word(inputWord);
          System.out.println(wordToTranslate.getTranslation());
      } else {
          if (args.length == 1){
              wordToTranslate = new Word(args[0]);
              System.out.println(wordToTranslate.getTranslation());
              } else{
              wordToTranslate = new Word(args[0], args[1]);
              System.out.println(wordToTranslate.getTranslation());
          }
      }
      wordToTranslate.connectSQL();
  }
}
