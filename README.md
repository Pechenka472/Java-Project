Морозов Андрей Андреевич
1. Вариант 5
2. Создал сущности:
    
    CountryStatistic - хранение информации распаршеного CSV файла, до её сохранения в БД

    CountryRank - элемент таблицы rank (столбцы: название страны, её рейтинг)

    CountryRegion - элемент таблицы region (столбцы: название строки, её регион)

    CountryScores - элемент таблицы scores (столбцы: название страны, разные очки...)

    Parser - основной класс с задачами

Я постарался сделать структуру БД максимально приближенную к нормальной третьей форме.

3. Распарсил CSV фаил и сохранил все данные:
![img.png](Screenshots/img.png)![img_1.png](Screenshots/img_1.png)
4-5. Создал БД SQLite и заполнил её соответсвующими объектам таблицами:
![img_3.png](Screenshots/img_3.png)
6. Все данные из набора объектов сохранил в таблицы БД
7. Сделал SQL-запросы в БД, для своих задач:
![img_5.png](Screenshots/img_5.png) ![img_4.png](Screenshots/img_4.png) ![img_6.png](Screenshots/img_6.png)
8. Полученные данные вывел в текстовом виде в консоль:
![img_7.png](Screenshots/img_7.png) ![img_8.png](Screenshots/img_8.png) ![img_9.png](Screenshots/img_9.png)
9. Числовые данные по заданию визуализировал в виде гистограммы:
![img_10.png](Screenshots/img_10.png)