package com.karankumar.bookproject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;



import static org.assertj.core.api.Assertions.*;

import java.util.List;


public class WebUItest {
    private static WebDriver driver;
    private static JavascriptExecutor executor;


    @BeforeAll
    public static void setup() {
        driver = new ChromeDriver();
        executor = (JavascriptExecutor)driver;
        driver.get("http://localhost:8080");
        login();
        addCustomShelfIfDontExists("test");
    }



    @Test
    public void testAddBookRead() {
        driver.get("http://localhost:8080/");
        addBook("Test book","John","Doe",
                "Read","11/11/2019","10/10/2020","Art",
                "123","5","Nice book","");
        waitFewSeconds();
        boolean isBookFind = findBook("Read","Test book");
        assertThat(isBookFind).isTrue();
    }

    @Test
    public void testAddBookToRead() {
        driver.get("http://localhost:8080/");
        addBook("Solo","Joanne","Uknown",
                "To read","","","Comedy",
                "4747","","","");
        waitFewSeconds();
        boolean isBookFind = findBook("To read","Solo");
        assertThat(isBookFind).isTrue();

    }

    @Test
    public void testAddBookDidNotFinish() {
        driver.get("http://localhost:8080/");
        addBook("Unfinished","Franz","Schubert",
                "Did not finish","11/12/2019","","Memoir",
                "4747","","","100");
        waitFewSeconds();
        boolean isBookFind = findBook("Did not finish","Unfinished");
        assertThat(isBookFind).isTrue();

    }

    @Test
    public void testAddBookReading() {
        driver.get("http://localhost:8080/");
        addBook("Progress","Uno","Dos",
                "Reading","11/12/2019","","Travel",
                "4747","","","");
        waitFewSeconds();
        boolean isBookFind = findBook("Reading","Progress");
        assertThat(isBookFind).isTrue();

    }

    @Test
    public void filterBooksByTitle() {
        driver.get("http://localhost:8080/");
        addBook("Something","Jona","Un",
                "To read","11/11/2019","11/11/2020","Comedy",
                "47","2","OK","");
        driver.get("http://localhost:8080/");
        addBook("Something else","Jonathan","Unknown",
                "To read","","","Comedy",
                "4747","","","");
        waitFewSeconds();
        findShelf("All books");
        filterByTitle("Something");
        assertThat(findFilteredBook("All books","Something")).isEqualTo(2);

    }

    @Test
    public void filterBooksByAuthor() {
        driver.get("http://localhost:8080/");
        addBook("Dimension 1","Phineas","Flyn",
                "To read","11/11/2019","11/11/2020","Comedy",
                "47","2","OK","");
        driver.get("http://localhost:8080/");
        addBook("Dimension 2","Phineas","Flyn",
                "To read","","","Comedy",
                "4747","","","");
        waitFewSeconds();
        findShelf("All books");
        filterByAuthor("Flyn");
        assertThat(findFilteredAuthor("All books","Flyn")).isEqualTo(2);

    }



    @Test
    public void testDeleteBook(){
        driver.get("http://localhost:8080/");
        addBook("Book to be deleted","Joel","Deleted",
                "Read","11/11/2019","10/10/2020","Fiction",
                "203","5","Not so good","");
        waitFewSeconds();
        findBook("Read","Book to be deleted");
        waitFewSeconds();
        deleteBook();
        waitFewSeconds();
        boolean isBookFind = findBook("Read","Book to be deleted");
        assertThat(isBookFind).isFalse();
    }

    @AfterEach
    public void deleteAll() {
        deleteAllTestBooks();
    }

    @AfterAll
    public static void close() {
        driver.close();

    }

    private static void login() {
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement name = inputs.get(0);
        WebElement password = inputs.get(1);
        WebElement button = driver.findElement(By.tagName("vaadin-button"));

        //log in
        name.sendKeys("user");
        password.sendKeys("password");
        button.click();
    }

    private static WebElement expandRootElement(WebElement element) {
        WebElement ele = (WebElement) ((JavascriptExecutor)driver)
                .executeScript("return arguments[0].shadowRoot", element);
        return ele;
    }

    private static void filterByTitle(String book) {
        WebElement vertical = driver.findElement(By.tagName("vaadin-vertical-layout"));
        WebElement horizontal = vertical.findElement(By.tagName("vaadin-horizontal-layout"));
        WebElement text_field = horizontal.findElement(By.tagName("vaadin-text-field"));
        WebElement shadow = expandRootElement(text_field);
        WebElement div = shadow.findElement(By.tagName("div"));
        WebElement div_2 = div.findElement(By.tagName("div"));
        WebElement slot = div_2.findElement(By.name("input"));
        WebElement input = slot.findElement(By.tagName("input"));
        input.sendKeys(book);
    }

    private static void filterByAuthor(String author) {
        WebElement vertical = driver.findElement(By.tagName("vaadin-vertical-layout"));
        WebElement horizontal = vertical.findElement(By.tagName("vaadin-horizontal-layout"));
        List<WebElement> text_field = horizontal.findElements(By.tagName("vaadin-text-field"));
        WebElement shadow = expandRootElement(text_field.get(1));
        WebElement div = shadow.findElement(By.tagName("div"));
        WebElement div_2 = div.findElement(By.tagName("div"));
        WebElement slot = div_2.findElement(By.name("input"));
        WebElement input = slot.findElement(By.tagName("input"));
        input.sendKeys(author);
    }

    private static void  findShelf(String shelf) {
        WebElement vertical = driver.findElement(By.tagName("vaadin-vertical-layout"));
        WebElement horizontal = vertical.findElement(By.tagName("vaadin-horizontal-layout"));
        WebElement combo_box = horizontal.findElement(By.tagName("vaadin-combo-box"));
        WebElement shadow = expandRootElement(combo_box);
        WebElement text_field = shadow.findElement(By.tagName("vaadin-text-field"));
        WebElement shadow_2 = expandRootElement(text_field);
        WebElement div = shadow_2.findElement(By.tagName("div"));
        WebElement div_2 = div.findElement(By.tagName("div"));
        WebElement slot = div_2.findElement(By.name("input"));
        WebElement input = slot.findElement(By.tagName("input"));
        input.sendKeys(shelf);
        input.sendKeys(Keys.ENTER);
    }

    private static int findFilteredBook(String shelf, String book) {
        int result = 0;

        waitFewSeconds();

        WebElement grid = driver.findElement(By.tagName("vaadin-grid"));
        WebElement shadow_grid = expandRootElement(grid);
        WebElement div_grid = shadow_grid.findElement(By.tagName("div"));
        WebElement table = div_grid.findElement(By.tagName("table"));
        List<WebElement> all_rows = table.findElements(By.tagName("tr"));

        waitFewSeconds();

        for(WebElement row: all_rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if(cols.size() > 0) {

                if(cols.get(0).getText().contains(book)) {
                   result++;
                }
            }
        }
        return result;
    }

    private static int findFilteredAuthor(String shelf, String author) {
        int result = 0;

        waitFewSeconds();

        WebElement grid = driver.findElement(By.tagName("vaadin-grid"));
        WebElement shadow_grid = expandRootElement(grid);
        WebElement div_grid = shadow_grid.findElement(By.tagName("div"));
        WebElement table = div_grid.findElement(By.tagName("table"));
        List<WebElement> all_rows = table.findElements(By.tagName("tr"));

        waitFewSeconds();

        for(WebElement row: all_rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            if(cols.size() > 0) {

                if(cols.get(1).getText().contains(author)) {
                    result++;
                }
            }
        }
        return result;
    }

    private static boolean findBook(String shelf, String book) {
        driver.get("http://localhost:8080/");
        findShelf(shelf);

        waitFewSeconds();

        WebElement grid = driver.findElement(By.tagName("vaadin-grid"));
        WebElement shadow_grid = expandRootElement(grid);
        WebElement div_grid = shadow_grid.findElement(By.tagName("div"));
        WebElement table = div_grid.findElement(By.tagName("table"));
        List<WebElement> all_rows = table.findElements(By.tagName("tr"));

        waitFewSeconds();

        for(WebElement row: all_rows) {
            List<WebElement> cols = row.findElements(By.tagName("td"));
            for(WebElement col: cols) {
                if(col.getText().contains(book)){
                    executor.executeScript("arguments[0].click();", col);
                    return true;
                }
            }
        }
        return false;
    }

    private static void waitFewSeconds() {
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    private static void deleteBook() {
        WebElement flow_component = driver.findElement(By.tagName("flow-component-renderer"));
        WebElement layout = flow_component.findElement(By.tagName("vaadin-horizontal-layout"));
        List<WebElement> book_buttons = layout.findElements(By.tagName("vaadin-button"));
        for(WebElement buttons: book_buttons){
            if(buttons.getText().contains("Delete")){
                executor.executeScript("arguments[0].click();", buttons);
            }
        }
    }


    private static  void addCustomShelfIfDontExists(String shelf) {
        List<WebElement> buttons = driver.findElements(By.tagName("vaadin-button"));
        WebElement addBookButton = buttons.get(0);

        addBookButton.click();

        WebElement dialog_overlay = driver.findElement(By.tagName("vaadin-dialog-overlay"));
        WebElement form = dialog_overlay.findElement(By.tagName("vaadin-form-layout"));
        WebElement form_item = form.findElement(By.tagName("vaadin-form-item"));
        WebElement text_field = form_item.findElement(By.tagName("vaadin-text-field"));
        WebElement shadow = expandRootElement(text_field);
        WebElement div = shadow.findElement(By.tagName("div"));
        List<WebElement> divs = div.findElements(By.tagName("div"));
        WebElement slot = divs.get(0).findElement(By.name("input"));
        WebElement input = slot.findElement(By.tagName("input"));
        input.sendKeys(shelf);


        WebElement button = form.findElement(By.tagName("vaadin-button"));

        try {
            button.click();
        } catch (ElementClickInterceptedException e){

        }




    }

    private static void deleteAllTestBooks () {
        driver.get("http://localhost:8080/");
        if (findBook("All books","Test book"))
        {
            deleteBook();
        }
        if(findBook("All books","Solo"))
        {
            deleteBook();
        }
        if(findBook("All books","Unfinished"))
        {
            deleteBook();
        }
        if(findBook("All books","Progress"))
        {
            deleteBook();
        }
        if(findBook("All books","Something else"))
        {
            deleteBook();
        }
        if(findBook("All books","Something"))
        {
            deleteBook();
        }
        if(findBook("All books","Dimension 1"))
        {
            deleteBook();
        }
        if(findBook("All books","Dimension 2"))
        {
            deleteBook();
        }
    }

    private static void addBook(String book, String firstNameAuthor,
                         String lastNameAuthor, String shelf,
                         String dateStarted, String dateFinish,String givenGenre,
                         String numPages, String valueRating,String givenReview,String numPagesRead) {


        List<WebElement> buttons = driver.findElements(By.tagName("vaadin-button"));
        WebElement addBookButton = buttons.get(1);

        addBookButton.click();

        List<WebElement> fields = driver.findElements(By.tagName("vaadin-form-item"));
        WebElement first = fields.get(0);
        WebElement firstTextElement = first.findElement(By.tagName("vaadin-text-field"));
        WebElement shadowRoot1 = expandRootElement(firstTextElement);
        WebElement div = shadowRoot1.findElement(By.className("vaadin-text-field-container"));
        WebElement divInside = div.findElement(By.id("vaadin-text-field-input-3"));
        WebElement slot = divInside.findElement(By.name("input"));
        WebElement input = slot.findElement(By.tagName("input"));
        input.sendKeys(book);

        WebElement second = fields.get(1);
        WebElement secondTextElement = second.findElement(By.tagName("vaadin-combo-box"));
        WebElement shadowRoot2 = expandRootElement(secondTextElement);
        WebElement div2 = shadowRoot2.findElement(By.tagName("vaadin-text-field"));
        WebElement shadowRoot = expandRootElement(div2);
        WebElement divInside2 = shadowRoot.findElement(By.tagName("div"));
        WebElement slot2 = divInside2.findElement(By.name("input"));
        WebElement input2 = slot2.findElement(By.tagName("input"));
        input2.sendKeys(shelf);

        WebElement second_shelf = fields.get(2);
        WebElement second_self_TextElement = second_shelf.findElement(By.tagName("vaadin-combo-box"));
        WebElement shadowRoot_shelf = expandRootElement(second_self_TextElement);
        WebElement div_shelf = shadowRoot_shelf.findElement(By.tagName("vaadin-text-field"));
        WebElement shadowRoot_shelf_2 = expandRootElement(div_shelf);
        WebElement divInside_shelf = shadowRoot_shelf_2.findElement(By.tagName("div"));
        WebElement slot_shelf = divInside_shelf.findElement(By.name("input"));
        WebElement input_shelf = slot_shelf.findElement(By.tagName("input"));
        input_shelf.sendKeys("test");

        WebElement third = fields.get(3);
        WebElement thirdTextElement = third.findElement(By.tagName("vaadin-text-field"));
        WebElement shadowRoot3 = expandRootElement(thirdTextElement);
        WebElement div3 = shadowRoot3.findElement(By.className("vaadin-text-field-container"));
        WebElement divInside3 = div3.findElement(By.tagName("div"));
        WebElement slot3 = divInside3.findElement(By.name("input"));
        WebElement input3 = slot3.findElement(By.tagName("input"));
        input3.sendKeys(firstNameAuthor);

        WebElement fourth = fields.get(4);
        WebElement fourthTextElement = fourth.findElement(By.tagName("vaadin-text-field"));
        WebElement shadowRoot4 = expandRootElement(fourthTextElement);
        WebElement div4 = shadowRoot4.findElement(By.className("vaadin-text-field-container"));
        WebElement divInside4 = div4.findElement(By.tagName("div"));
        WebElement slot4 = divInside4.findElement(By.name("input"));
        WebElement input4 = slot4.findElement(By.tagName("input"));
        input4.sendKeys(lastNameAuthor);

        if(shelf == "Read" || shelf == "Reading" || shelf == "Did not finish") {
            WebElement date_s = fields.get(5);
            WebElement date_picker_s = date_s.findElement(By.tagName("vaadin-date-picker"));
            WebElement shadowRoot_date_s = expandRootElement(date_picker_s);
            WebElement date_text_field = shadowRoot_date_s.findElement(By.tagName("vaadin-date-picker-text-field"));
            WebElement shadowRoot_date_s_2 = expandRootElement(date_text_field);
            WebElement div_s = shadowRoot_date_s_2.findElement(By.tagName("div"));
            WebElement div_s_2 = div_s.findElement(By.name("input"));
            WebElement input_s = div_s_2.findElement(By.tagName("input"));
            input_s.sendKeys(dateStarted);
        }

        if(shelf == "Read") {
            WebElement date_f = fields.get(6);
            WebElement date_picker_f = date_f.findElement(By.tagName("vaadin-date-picker"));
            WebElement shadowRoot_date_f = expandRootElement(date_picker_f);
            WebElement date_text_field_f = shadowRoot_date_f.findElement(By.tagName("vaadin-date-picker-text-field"));
            WebElement shadowRoot_date_f_2 = expandRootElement(date_text_field_f);
            WebElement div_f = shadowRoot_date_f_2.findElement(By.tagName("div"));
            WebElement div_f_2 = div_f.findElement(By.name("input"));
            WebElement input_f = div_f_2.findElement(By.tagName("input"));
            input_f.sendKeys(dateFinish);
        }

        WebElement genre = fields.get(7);
        WebElement genre_TextElement = genre.findElement(By.tagName("vaadin-combo-box"));
        WebElement shadowRoot_genre = expandRootElement(genre_TextElement);
        WebElement div_genre = shadowRoot_genre.findElement(By.tagName("vaadin-text-field"));
        WebElement shadowRoot_genre_2 = expandRootElement(div_genre);
        WebElement divInside_genre = shadowRoot_genre_2.findElement(By.tagName("div"));
        WebElement slot_genre = divInside_genre.findElement(By.name("input"));
        WebElement input_genre = slot_genre.findElement(By.tagName("input"));
        input_genre.sendKeys(givenGenre);

        if(shelf == "Did not finish") {
            WebElement read_pages = fields.get(8);
            WebElement read_pages_TextElement = read_pages.findElement(By.tagName("vaadin-integer-field"));
            WebElement shadowRoot_pages_read = expandRootElement(read_pages_TextElement);
            WebElement div_pages_read = shadowRoot_pages_read.findElement(By.tagName("div"));
            WebElement divInside_pages_read = div_pages_read.findElement(By.tagName("div"));
            WebElement slot_pages_read = divInside_pages_read.findElement(By.name("input"));
            WebElement input_pages_read = slot_pages_read.findElement(By.tagName("input"));
            input_pages_read.sendKeys(numPagesRead);
        }
        WebElement pages = fields.get(9);
        WebElement pages_TextElement = pages.findElement(By.tagName("vaadin-integer-field"));
        WebElement shadowRoot_pages = expandRootElement(pages_TextElement);
        WebElement div_pages = shadowRoot_pages.findElement(By.tagName("div"));
        WebElement divInside_pages = div_pages.findElement(By.tagName("div"));
        WebElement slot_pages = divInside_pages.findElement(By.name("input"));
        WebElement input_pages = slot_pages.findElement(By.tagName("input"));
        input_pages.sendKeys(numPages);

        if(shelf == "Read") {
            WebElement rating = fields.get(10);
            WebElement rating_TextElement = rating.findElement(By.tagName("vaadin-number-field"));
            WebElement shadowRoot_rating = expandRootElement(rating_TextElement);
            WebElement div_rating = shadowRoot_rating.findElement(By.tagName("div"));
            WebElement divInside_rating = div_rating.findElement(By.tagName("div"));
            WebElement slot_rating = divInside_rating.findElement(By.name("input"));
            WebElement input_rating = slot_rating.findElement(By.tagName("input"));
            input_rating.sendKeys(valueRating);
            input_rating.sendKeys(Keys.ENTER);
        }

        if(shelf == "Read") {
            WebElement review = fields.get(13);
            WebElement review_textElement = review.findElement(By.tagName("vaadin-text-area"));
            WebElement shadow_text = expandRootElement(review_textElement);
            WebElement div_text = shadow_text.findElement(By.tagName("div"));
            WebElement div_inside_text = div_text.findElement(By.tagName("div"));
            WebElement slot_text = div_inside_text.findElement(By.name("textarea"));
            WebElement textarea = slot_text.findElement(By.tagName("textarea"));
            textarea.sendKeys(givenReview);
        }

        WebElement flow_component = driver.findElement(By.tagName("flow-component-renderer"));
        WebElement buttons_book = flow_component.findElement(By.tagName("vaadin-horizontal-layout"));
        WebElement add_book_button = buttons_book.findElement(By.tagName("vaadin-button"));

        executor.executeScript("arguments[0].click();", add_book_button);

    }

}
