import React from 'react';
import {FlexDiv, FormLabel} from "./BasicStyles";
import DatePicker from "react-datepicker/es";
import {Link} from "react-router-dom";
import {Button} from "./Button";


import styled from "styled-components";
const TopDiv = styled(FlexDiv)`
   justify-content: space-between;
   margin-bottom: 5em;
`;


export function TopSectionCalendarPage(props) {
    return <TopDiv>
        <FlexDiv>
            <div>
                <FormLabel>Fra:</FormLabel>
                <DatePicker onSelect={(date)=> props.onFromDateChange(date)} selected={props.fromDate}/>
            </div>
            <div style={{marginLeft: '1em'}}>
                <FormLabel>Til:</FormLabel>
                <DatePicker onSelect={(date)=> {
                    props.onTodoDateChange(date);
                }} selected={props.toDate} />
            </div>
        </FlexDiv>
        {props.hasPermission &&
        <Link to={'/calendar/create/examination'}>
            <Button >Ny eksemenasjon</Button>
        </Link>
        }

    </TopDiv>
}