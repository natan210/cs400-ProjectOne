import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * this class models the front end component of the Movie Mapper project
 */
public class Frontend {
  static Scanner in = new Scanner(System.in);

  /**
   * this method runs the frontend
   * @param be backend instance to run
   */
  public static void run(BackendInterface be) {
    baseMode(be); // run base mode on initialization
  }

  /**
   * this method runs the base mode of the front end
   * @param be backend instance to run
   */
  private static void baseMode(BackendInterface be) {
    List<MovieInterface> top;
    int numMovies = be.getNumberOfMovies();
    int numPages = numMovies / 3;
    if (numMovies % 3 != 0) {
      numPages++; // account for any partial pages
    }
    System.out.println("/////////////////////////////////////////////////");
    System.out.println("/ Welcome to Group BD Team Blue's Movie Mapper! /");
    System.out.println("/////////////////////////////////////////////////\n");

    System.out.println("-----------------Top 3 Movies----------------");
    System.out.println("(use keys 1-" + numPages + " to select which movie to view!)");



    String commandPrompt = "\n============================COMMANDS================================"
        + "\n(x) - Exit    (1-" + numPages +") - Select Page    (g) - Genre Selection   (r) - "
        + "Rating Selection\n";
    System.out.println(commandPrompt);

    // handle input (i understand theres no reason to instantiate the Scanner, but for whatever
    // reason, this is the only way that the InputStreamSimulator in the tests work
    in = new Scanner(System.in);
    boolean viewing = true;
    String input;
    int ranking = 1;

    while (viewing) {
      if (in.hasNextLine()) {
        input = in.nextLine();
        try {
          int page = Integer.parseInt(input);
          if (page >= 1 && page <= numPages) {
            top = be.getThreeMovies(ranking - 1);
            for (int i = 0; i < 3; i++) {
              MovieInterface m = top.get(i);
              System.out.println(printMovie(m, ranking));
              ranking++;
            }
          } else {
            System.out.println("You've reached the end of the list! Try refining your search by " + "going to genre selection mode or rating selection mode");
          }
          System.out.println(commandPrompt);
        } catch (NumberFormatException e) {
          if (input.equals("r")) { // switches to rating selection mode
            viewing = false;
            ratingSelection(be);
          } else if (input.equals("g")) { // switches to genre selection mode
            viewing = false;
            genreSelection(be);
          } else if (input.equals("x")) { // exits the application
            viewing = false;
            System.out.println("Exiting...");
          } else {
            System.out.println("Invalid Command!");
          }
        }
      } else {
        viewing = false;
      }
    }
  }


  /**
   * this method runs the genre selection mode of the frontend
   * @param be back end instance to run
   */
  private static void genreSelection(BackendInterface be) {
    System.out.println("\n--------------------GENRE SELECTION-------------------");
    System.out.println("(use the number keys to select which genres to filter by!)\n");

    // initialize variables
    List<String> genreList = be.getAllGenres();
    String[] selected = new String[genreList.size()]; // array that corresponds with genreList
    String commandPrompt = "\n==================COMMANDS======================"
        + "\n(x) - Back to Base Mode    (1-" + genreList.size() +") Select Genre\n";

    // initialize selected
    Arrays.fill(selected, "[ ]");

    System.out.println(listGenres(genreList, selected, commandPrompt));

    // handle input
    //Scanner in = new Scanner(System.in);
    boolean viewing = true;
    String input;

    while (viewing) {
      if (in.hasNextLine()) {
        input = in.nextLine();
        try {
          int genreNum = Integer.parseInt(input);

          if (genreNum >= 1 && genreNum <= genreList.size()) { // input is a genre selection

            // handle selection/deselection
            if (selected[genreNum - 1].equals("[ ]")) {
              selected[genreNum - 1] = "[x]";
              be.addGenre(genreList.get(genreNum - 1));
            } else if (selected[genreNum - 1].equals("[x]")) {
              selected[genreNum - 1] = "[ ]";
              be.removeGenre(genreList.get(genreNum - 1));
            }
            System.out.println(listGenres(genreList, selected, commandPrompt)); // update list
          } else {
            System.out.println("Invalid command!");
          }
        } catch (NumberFormatException e) {
          // if input is not a number, check if it's 'x'
          if (input.equals("x")) {
            viewing = false;
            System.out.println("Sending you back to base mode...");
            baseMode(be); // send back to base mode
          } else {
            System.out.println("Invalid Command!");
          }
        }
      }
      else {
        viewing = false;
      }
    }
  }

  /**
   * this method runs the rating selection mode of the front end
   * @param be back end instance to run
   */
  private static void ratingSelection(BackendInterface be) {
    System.out.println("\n-----------------RATING SELECTION-----------------");
    System.out.println("(use the number keys to select ratings to filter by!)\n");

    // initialize variables
    String[] selected = new String[10];
    Arrays.fill(selected, "[ ]");
    String commandPrompt = "\n==================COMMANDS======================"
        + "\n(x) - Back to Base Mode    (1-10) Select Rating\n";

    // handle input
    //Scanner in = new Scanner(System.in);
    boolean viewing = true;
    String input;

    System.out.println(listRatings(selected, commandPrompt));

    while (viewing) {
      if (in.hasNextLine()) {
        input = in.nextLine();
        try {
          int rating = Integer.parseInt(input);

          // handle selection/deselection
          if (rating > 0 && rating <= 10) {
            if (selected[rating - 1].equals("[ ]")) {
              be.addAvgRating(input);
              selected[rating - 1] = "[x]";
              System.out.println(listRatings(selected, commandPrompt));
            } else if (selected[rating - 1].equals("[x]")) {
              be.removeAvgRating(input);
              selected[rating - 1] = "[ ]";
              System.out.println(listRatings(selected, commandPrompt));
            }
          } else {
            System.out.println("Invalid Command!");
          }
        } catch (NumberFormatException e) {
          // if input is not a number, check if it's 'x'
          if (input.equals("x")) {
            viewing = false;
            System.out.println("Sending you back to base mode...");
            baseMode(be); // send back to base mode
          } else {
            System.out.println("Invalid Command!");
          }
        }
      }
      else {
        viewing = false;
      }
    }
  }

  /**
   * this helper method lists all genres and whether or not they're selected for the genre
   * selection mode
   * @param genres List of genres to print
   * @param selected Array of Strings that can either be [ ] or [x] depending whether the genre
   *                 at the corresponding index of genres[] is selected.
   * @param cPrompt command prompt to display after listing genres
   * @return a String that lists all genres and whether or not they're selected for the genre
   * selection mode
   */
  private static String listGenres(List<String> genres, String[] selected, String cPrompt) {
    String res = "";
    for (int i = 0; i < genres.size(); i++) {
      res += selected[i] + " " + (i+1) + ". " + genres.get(i) + "\n";
    }
    res += cPrompt;
    return res;
  }

  /**
   * this helper method lists all ratings and whether or not they're selected for the ratings
   * selection mode
   * @param selected Array of Strings that can either be [ ] or [x] depending whether the
   *                 corresponding rating is selected
   * @param cPrompt command prompt to display after listing genres
   * @returna String that lists all ratings (1-10) and whether or not they're selected for the
   * rating selection mode
   */
  private static String listRatings(String[] selected, String cPrompt) {
    String res = "";
    for (int i = 0; i < 10; i++) {
      res += selected[i] + " " + (i+1) + "\n";
    }
    res += cPrompt;
    return res;
  }

  /**
   * this helper method constructs a String with movie information
   * @param m movie to get info from
   * @param ranking rank of the movie (by avg vote)
   * @return a String listing info about a movie
   */
  private static String printMovie(MovieInterface m, int ranking) {
    String res = "";
    res += "\n-----------------RANK #"+ ranking + "----------------\n";

    res += "Title: " + m.getTitle();
    res += "\nYear: " + m.getYear();
    res += "\nGenres: ";

    // get genres
    List<String> genres = m.getGenres();
    for (int i = 0; i < genres.size(); i++) {
      if (i != genres.size() - 1) {
        res += genres.get(i) + ", "; // handle comma placement
      }
      else {
        res += genres.get(i);
      }
    }

    // continue printing director, description, and avg vote
    res += "\nDirected By: " + m.getDirector();
    res += "\nDescription: " + m.getDescription();
    res += "\nAverage Vote: " + m.getAvgVote();

    return res;
  }

  /**
   * main method to run the Movie Mapper program from
   * @param args command line args (path to csv file)
   */
  public static void main (String[] args) {
    BackendInterface be = new BackendDummy(args); // TODO: change this to Backend
    run(be);
  }
}
