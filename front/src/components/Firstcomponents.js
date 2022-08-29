import React from 'react';
import './Firstcomponents.css';

const Firstcomponents = () => {
    return (
        <div className='centro1'>
            <img className='doouglas' src='/Douglas.png' alt='douglas'/>
            <div id="divBusca">
                <input type="text" id="txtBusca" placeholder="Pesquise no Doouglas ou escreva um URL " />
                <div className='btnBusca'>
                    <img src='/iconssearch.png' id="btnBusca" alt="Buscar" />
                </div>
            </div>
        </div>
    )
}

export default Firstcomponents