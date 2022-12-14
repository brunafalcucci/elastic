import React, { useState } from 'react';
import './Firstcomponents.css';
import axios from 'axios';

const Firstcomponents = () => {
    const [search, setSearch]=useState('');
    const [response, setResponse]=useState(null);
    const [pageNumber,setPageNumber] = useState(1);

    function searchOnDoouglas(busca) {
        axios.defaults.headers.post['Content-Type'] = 'application/json;charset=utf-8';
        axios.defaults.headers.post['Access-Control-Allow-Origin'] = '*';
        axios.get(`http://localhost:8080/${busca}/ / /${pageNumber}`).then(
            (resposta) => {
                setResponse(resposta.data);
            })
    }

    let definePage = (typeOfPage) =>{
        if (typeOfPage === "next"){
            setPageNumber(pageNumber + 1)
        }
        else{
            if(pageNumber > 1 ){
                setPageNumber(pageNumber - 1);
            }
        }
        searchOnDoouglas(search)
    }

    if(response == null) {
        return (
            <div className='centro1'>
                <img className='doouglas' src='/Douglas.png' alt='douglas'/>
                <div id="divBusca">
                    <input type="text" id="txtBusca" value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Pesquise no Doouglas ou escreva um URL " />
                    <div className='btnBusca'>
                        <img src='/iconssearch.png' id="btnBusca" alt="Buscar" onClick={() => searchOnDoouglas(search)}/>
                    </div>
                </div>
            </div>
        )
    } else {
        return (
            <div className='centro2'>
                {response.map(e => {
                    return <div key={e.url} className='showResponse'><a href={e.url}>{e.title}</a><p>{e.content}</p></div>
                })}
                <div className='bang'>
                <p onClick={() => definePage("previuous")}>Previuos page</p>
                <p onClick={() => definePage("next")}>Next page</p>
                </div>
            </div>
        )
    }
}

export default Firstcomponents