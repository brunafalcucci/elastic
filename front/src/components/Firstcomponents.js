import React, { useState } from 'react';
import './Firstcomponents.css';
import axios from 'axios';

const Firstcomponents = () => {
    const [search, setSearch]=useState('');
    const [response, setResponse]=useState(null);

    function searchOnDoouglas(busca) {
        axios.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8';
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.get(`http://localhost:8080/${busca}/teste/${busca}/1`).then(
            (resposta) => {
                setResponse(resposta.data);
            })
    }

    if(response == null) {
        return (
            <div className='centro1'>
                <img className='doouglas' src='/Douglas.png' alt='douglas'/>
                <div id="divBusca">
                    <input type="text" id="txtBusca" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Pesquise no Doouglas ou escreva um URL " />
                    <div className='btnBusca'>
                        <img src='/iconssearch.png' id="btnBusca" alt="Buscar" onClick={searchOnDoouglas(search)}/>
                    </div>
                </div>
            </div>
        )
    } else {
        return (
            <div className='centro2'>
                <div className='showResponse'>
                    <a className='urlLink' target="_blank" rel='noreferrer' href={response.url}>{response.url}</a>
                </div>
                <div className='showResponse'>
                    <a className='titleLink' target="_blank" rel='noreferrer' href={response.url}>{response.title}</a>
                </div>
                <div className='showResponse'>
                    <span className='contentSpan'>{response.content}</span>
                </div>
            </div>
        )
    }
}

export default Firstcomponents