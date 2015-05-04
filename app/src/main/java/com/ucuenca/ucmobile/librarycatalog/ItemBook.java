package com.ucuenca.ucmobile.librarycatalog;

/**
 * Created by Usuario on 29/04/15.
 */
public class ItemBook {
    protected long Id;
    protected String uri;
    protected String titulo;
    protected String autor;

    public ItemBook(long id, String uri, String titulo, String autor) {
        Id = id;
        this.uri = uri;
        this.titulo = titulo;
        this.autor = autor;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
