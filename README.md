**Projekt Sędzia - Algorytmy I Struktury Danych**

_Szymon Chmal i Konrad Łyś_

**1. Ogólny zarys projektu**

    Projekt jest realizacją programu oceniającego graczy zgodnie z protokołem jak poniżej :
        1. Planszą jest pole o wymiarach n * n gdzie n nieparzyste
        2. Elementy(prostokąty) o rozmiarze 2x1 mogą być umieszczane poziomio lub pionowo
        3. Gracze wstawiają elementy na przemian
        4. Wygrywa ten który ostatni położy swój prostokąt
        5. Do 1000 pól
        6. Plansza może na początku być zapełniona kwadratami 1x1
        7. Czas generowania ruchu mniej niż pół sekundy
        8. Komunikacja wygląda w następujący sposób (przykładowe wartości) :
                1. s -> p1 : n_2x3_4x5
                2. p1 -> s : ok < 1s
                3. s -> p2 : n[_2x3[_4x5]]
                4. p2 -> s : ok < 1s
                5. s -> p1 : start
                6. P1 -> 21x21_21x22 
                7. s -> P2 : 21x21_21x22
                A: przekroczenie czasu : oba -> stop
                B: błędny ruch : oba -> stop
                C: koniec gry : oba -> stop
        9. Programy graczy muszą być umieszczone w jednym folderze, gdzie znajdują się podfoldery poszczególnych
           graczy. W każdym podfolderze znajduje sie plik wykonywanlny in plik info.txt zawierający
           w pierwszej linijce nazwe gracza, w drugiej sposób wykonania programu.

**2. Struktura programu**

    Program jest zrealizowany zgodnie ze wzorcem MVC, przy czym za realizację logiki odpowiadają serwisy GameService i LoaderService.
    Program napisano w języku Java(ver. 8) korzystjąc z frameworka Spring. Baza danych to baza wbudowana h2.
    
    //TODO dodaj opis gui
    
**3. Uruchamianie programu**

    //TODO Jak to właściwie odpalic ? ikona czy java -jar ....