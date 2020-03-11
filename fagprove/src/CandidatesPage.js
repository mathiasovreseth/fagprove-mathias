import React, { useContext, useEffect } from 'react';
import { FlexDiv } from './BasicStyles';
import { Button } from './Button';
import { Link } from 'react-router-dom';
import { IsAdminContext, superRequest } from './App';
import styled from 'styled-components';


const OuterDiv = styled.div`
table, th, td {
  border: 1px solid black;
}
`;


export function CandidatesPage() {
  const [candidates, setCandidates] = React.useState();
  const adminConsumer = useContext(IsAdminContext);
  const hasPemission = adminConsumer.role === 'ROLE_ADMIN' || adminConsumer.role === 'ROLE_MANAGER';

  async function getCandidates() {
    const fetch = await superRequest('http://localhost:8080/person/listCandidates', {});
    const res = await fetch;

    setCandidates(res);
    console.log(res);
  }


  useEffect(() => {
    getCandidates();
  }, []);


  return (
    <OuterDiv style={{width: '100%'}}>
      <FlexDiv style={{width: '100%', justifyContent: 'flex-end'}}>
        {hasPemission &&
        <Link to={'/create/user'}>
          <Button>Opprett </Button>
        </Link>
        }

      </FlexDiv>
        <table>
          <tbody>
          <tr >
            <th >Navn</th>
            <th>Mobil</th>
            <th>bedrift</th>
            <th>stad</th>
            <th>oppmeldt</th>
            <th>Avtalt</th>
            <th>start</th>
            <th>slutt</th>
            <th>Leiar</th>
            <th>Medlem</th>
          </tr>

          {candidates && candidates?.map(c => {
            const startDate = new Date(new Date(c.examinationStartDate).getFullYear(), new Date(c?.examinationStartDate).getMonth(),new Date(c?.examinationStartDate).getDate() );
            const endDate = new Date(new Date(c.examinationEndDate).getFullYear(), new Date(c?.examinationEndDate).getMonth(),new Date(c?.examinationEndDate).getDate() );
            return <tr style={{marginLeft: '2em'}}>
              <td>{c.name}</td>
              <td>{c?.phoneNumber ?? ''}</td>
              <td>{c?.company ?? ''}</td>
              <td>{c?.region ?? ''}</td>
              <td>{c?.registrationReceived ? 'Ja': 'Nei'}</td>
              <td>{c?.examinationPlanned ? 'Ja': 'Nei'}</td>
              <td>{c.examinationPlanned ? startDate.toDateString() ?? '': 'Ikkje satt'}</td>
              <td>{c.examinationPlanned ? endDate.toDateString() ?? '': 'ikkje satt'}</td>
              <td>{c?.examinationResponsible?.name ?? 'Ikkje satt'}</td>
              <td>{c?.examinationSecondary?.name ?? 'Ikkje satt'}</td>
            </tr>
          })}

          </tbody>
        </table>
    </OuterDiv>
  )
}